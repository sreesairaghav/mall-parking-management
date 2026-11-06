package com.mallparking.service;

import com.mallparking.dto.EntryRequest;
import com.mallparking.dto.ExitRequest;
import com.mallparking.dto.ParkingSessionResponse;
import com.mallparking.entity.*;
import com.mallparking.entity.enums.*;
import com.mallparking.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ParkingService {
    private final VehicleRepository vehicleRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingSlotRepository slotRepository;
    private final ParkingSessionRepository sessionRepository;
    private final ParkingEventRepository eventRepository;
    private final ParkingRateRepository rateRepository;
    private final PaymentRepository paymentRepository;

    public ParkingService(VehicleRepository vehicleRepository, ParkingFloorRepository floorRepository,
                          ParkingSlotRepository slotRepository, ParkingSessionRepository sessionRepository,
                          ParkingEventRepository eventRepository, ParkingRateRepository rateRepository,
                          PaymentRepository paymentRepository) {
        this.vehicleRepository = vehicleRepository;
        this.floorRepository = floorRepository;
        this.slotRepository = slotRepository;
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
        this.rateRepository = rateRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ParkingSessionResponse vehicleEntry(EntryRequest request) {
        String registration = request.getRegistrationNumber().trim().toUpperCase();
        Vehicle vehicle = vehicleRepository.findByRegistrationNumberIgnoreCase(registration)
                .orElseGet(() -> vehicleRepository.save(new Vehicle(
                        registration, request.getDriverName().trim(),
                        request.getPhoneNumber(), request.getVehicleType())));

        if (sessionRepository.findFirstByVehicleIdAndSessionStatusOrderByEntryTimeDesc(
                vehicle.getId(), SessionStatus.ACTIVE).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This vehicle already has an active parking session.");
        }

        List<ParkingSlot> candidates;
        if (request.getFloorCode() != null && !request.getFloorCode().isBlank()) {
            ParkingFloor floor = floorRepository.findByFloorCode(request.getFloorCode().trim().toUpperCase())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found."));
            candidates = slotRepository.findByParkingFloorIdAndStatus(floor.getId(), SlotStatus.AVAILABLE);
        } else {
            candidates = slotRepository.findByStatus(SlotStatus.AVAILABLE);
        }

        ParkingSlot chosen = candidates.stream()
                .filter(slot -> sessionRepository.findFirstByParkingSlotIdAndSessionStatus(
                        slot.getId(), SessionStatus.ACTIVE).isEmpty())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "No available parking slot was found."));

        ParkingSession session = sessionRepository.save(
                new ParkingSession(vehicle, chosen, LocalDateTime.now()));
        eventRepository.save(new ParkingEvent(EventType.ENTRY, session, "Vehicle entry recorded"));
        return new ParkingSessionResponse(session);
    }

    @Transactional
    public ParkingSessionResponse vehicleExit(Long sessionId, ExitRequest request) {
        ParkingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking session not found."));

        if (session.getSessionStatus() != SessionStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This parking session is already completed.");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        long minutes = Math.max(0, Duration.between(session.getEntryTime(), exitTime).toMinutes());
        int durationMinutes = Math.toIntExact(minutes);
        ParkingRate rate = rateRepository.findFirstByActiveTrueOrderByEffectiveFromDesc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "No active parking rate is configured."));

        BigDecimal fee = calculateFee(durationMinutes, rate);
        session.setExitTime(exitTime);
        session.setDurationMinutes(durationMinutes);
        session.setCalculatedFee(fee);
        session.setSessionStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);

        Payment payment = new Payment(fee, request.getPaymentMethod(), session);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentTime(exitTime);
        payment.setTransactionReference("MP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 18).toUpperCase());
        paymentRepository.save(payment);

        eventRepository.save(new ParkingEvent(EventType.EXIT, session, "Vehicle exit and payment recorded"));
        return new ParkingSessionResponse(session);
    }

    private BigDecimal calculateFee(int durationMinutes, ParkingRate rate) {
        if (durationMinutes <= rate.getGracePeriodMinutes()) return BigDecimal.ZERO;
        if (durationMinutes >= 24 * 60) return rate.getDayPassRate();

        int billableMinutes = Math.max(0, durationMinutes - rate.getGracePeriodMinutes());
        int billableHours = Math.max(1, (int) Math.ceil(billableMinutes / 60.0));
        BigDecimal extraHours = BigDecimal.valueOf(Math.max(0, billableHours - 1));
        return rate.getFirstHourRate().add(rate.getSubsequentHourRate().multiply(extraHours));
    }
}

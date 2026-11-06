package com.mallparking.service;

import com.mallparking.dto.*;
import com.mallparking.entity.*;
import com.mallparking.entity.enums.*;
import com.mallparking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DashboardService {
    private final ParkingFloorRepository floorRepository;
    private final ParkingSlotRepository slotRepository;
    private final ParkingSessionRepository sessionRepository;
    private final PaymentRepository paymentRepository;
    private final ParkingEventRepository eventRepository;

    public DashboardService(ParkingFloorRepository floorRepository, ParkingSlotRepository slotRepository,
                            ParkingSessionRepository sessionRepository, PaymentRepository paymentRepository,
                            ParkingEventRepository eventRepository) {
        this.floorRepository = floorRepository;
        this.slotRepository = slotRepository;
        this.sessionRepository = sessionRepository;
        this.paymentRepository = paymentRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStats getSummary() {
        int total = Math.toIntExact(slotRepository.count());
        int occupied = Math.toIntExact(sessionRepository.countBySessionStatus(SessionStatus.ACTIVE));
        int reserved = Math.toIntExact(slotRepository.countByStatus(SlotStatus.RESERVED));
        int maintenance = Math.toIntExact(slotRepository.countByStatus(SlotStatus.MAINTENANCE));
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        BigDecimal revenue = paymentRepository.findByPaymentTimeBetween(start, end).stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PAID)
                .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        int entriesToday = Math.toIntExact(eventRepository.findByEventType(EventType.ENTRY).stream()
                .filter(e -> !e.getEventTime().isBefore(start) && e.getEventTime().isBefore(end)).count());

        DashboardStats stats = new DashboardStats(total, occupied);
        stats.setAvailableSlots(Math.max(0, total - occupied - reserved - maintenance));
        stats.setTodayRevenue(revenue.doubleValue());
        stats.setCarsParkedToday(entriesToday);
        return stats;
    }

    @Transactional(readOnly = true)
    public List<FloorStats> getFloorStats() {
        return floorRepository.findByActiveTrue().stream().map(floor -> {
            int total = Math.toIntExact(slotRepository.countByParkingFloorId(floor.getId()));
            int occupied = Math.toIntExact(sessionRepository.findBySessionStatus(SessionStatus.ACTIVE).stream()
                    .filter(s -> s.getParkingSlot().getParkingFloor().getId().equals(floor.getId())).count());
            int reserved = Math.toIntExact(slotRepository.countByParkingFloorIdAndStatus(floor.getId(), SlotStatus.RESERVED));
            int maintenance = Math.toIntExact(slotRepository.countByParkingFloorIdAndStatus(floor.getId(), SlotStatus.MAINTENANCE));
            return new FloorStats(floor.getFloorName(), floor.getFloorCode(), total,
                    occupied, Math.max(0, total - occupied - reserved - maintenance));
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentActivity() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return eventRepository.findByEventTimeBetweenOrderByEventTimeDesc(start, end).stream()
                .limit(10)
                .map(event -> {
                    ParkingSession session = event.getParkingSession();
                    return new ActivityLog(
                            session.getVehicle().getRegistrationNumber(),
                            session.getParkingSlot().getParkingFloor().getFloorCode(),
                            session.getParkingSlot().getSlotNumber(),
                            event.getEventTime().format(DateTimeFormatter.ofPattern("hh:mm a")),
                            event.getEventType() == EventType.ENTRY
                                    ? ActivityLog.EventType.ENTRY : ActivityLog.EventType.EXIT,
                            session.getVehicle().getDriverName());
                }).toList();
    }
}

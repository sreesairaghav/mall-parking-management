package com.mallparking.controller;

import com.mallparking.dto.*;
import com.mallparking.entity.*;
import com.mallparking.entity.enums.*;
import com.mallparking.repository.*;
import com.mallparking.service.DashboardService;
import com.mallparking.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api")
public class ParkingApiController {
    private final ParkingService parkingService;
    private final DashboardService dashboardService;
    private final VehicleRepository vehicleRepository;
    private final ParkingSlotRepository slotRepository;
    private final ParkingSessionRepository sessionRepository;
    private final ParkingEventRepository eventRepository;
    private final PaymentRepository paymentRepository;
    private final ParkingRateRepository rateRepository;
    private final NotificationSettingRepository notificationRepository;

    public ParkingApiController(ParkingService parkingService, DashboardService dashboardService,
                                VehicleRepository vehicleRepository, ParkingSlotRepository slotRepository,
                                ParkingSessionRepository sessionRepository,
                                ParkingEventRepository eventRepository,
                                PaymentRepository paymentRepository,
                                ParkingRateRepository rateRepository,
                                NotificationSettingRepository notificationRepository) {
        this.parkingService = parkingService;
        this.dashboardService = dashboardService;
        this.vehicleRepository = vehicleRepository;
        this.slotRepository = slotRepository;
        this.sessionRepository = sessionRepository;
        this.eventRepository = eventRepository;
        this.paymentRepository = paymentRepository;
        this.rateRepository = rateRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/dashboard/summary")
    public DashboardStats summary() { return dashboardService.getSummary(); }

    @GetMapping("/dashboard/floors")
    public List<FloorStats> floors() { return dashboardService.getFloorStats(); }

    @GetMapping("/dashboard/activity")
    public List<ActivityLog> activity() { return dashboardService.getRecentActivity(); }

    @GetMapping("/slots")
    public List<SlotResponse> slots(@RequestParam(required = false) String floorCode) {
        List<ParkingSlot> slots = slotRepository.findAll();
        return slots.stream()
                .filter(s -> floorCode == null || s.getParkingFloor().getFloorCode().equalsIgnoreCase(floorCode))
                .map(s -> new SlotResponse(s, sessionRepository.findFirstByParkingSlotIdAndSessionStatus(
                        s.getId(), SessionStatus.ACTIVE).isPresent()))
                .toList();
    }

    @GetMapping("/vehicles")
    public List<VehicleResponse> vehicles() {
        return vehicleRepository.findAll().stream().map(VehicleResponse::new).toList();
    }

    @GetMapping("/vehicles/{registrationNumber}")
    public VehicleResponse vehicle(@PathVariable String registrationNumber) {
        Vehicle v = vehicleRepository.findByRegistrationNumberIgnoreCase(registrationNumber)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehicle not found."));
        return new VehicleResponse(v);
    }

    @PostMapping("/parking/entry")
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingSessionResponse entry(@Valid @RequestBody EntryRequest request) {
        return parkingService.vehicleEntry(request);
    }

    @PostMapping("/parking/exit/{sessionId}")
    public ParkingSessionResponse exit(@PathVariable Long sessionId, @Valid @RequestBody ExitRequest request) {
        return parkingService.vehicleExit(sessionId, request);
    }

    @GetMapping("/parking/active")
    public List<ParkingSessionResponse> activeSessions() {
        return sessionRepository.findBySessionStatus(SessionStatus.ACTIVE)
                .stream().map(ParkingSessionResponse::new).toList();
    }

    @GetMapping("/events")
    public List<Map<String, Object>> events(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String date) {
        LocalDate selected = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        List<ParkingEvent> rows = eventRepository.findByEventTimeBetweenOrderByEventTimeDesc(
                selected.atStartOfDay(), selected.atTime(LocalTime.MAX));
        return rows.stream()
                .filter(e -> type == null || type.isBlank() || e.getEventType().name().equalsIgnoreCase(type))
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    ParkingSession s = e.getParkingSession();
                    m.put("id", e.getId());
                    m.put("type", e.getEventType().name());
                    m.put("eventTime", e.getEventTime());
                    m.put("remarks", e.getRemarks());
                    m.put("registrationNumber", s.getVehicle().getRegistrationNumber());
                    m.put("driverName", s.getVehicle().getDriverName());
                    m.put("floorCode", s.getParkingSlot().getParkingFloor().getFloorCode());
                    m.put("slotNumber", s.getParkingSlot().getSlotNumber());
                    m.put("durationMinutes", s.getDurationMinutes());
                    m.put("amount", s.getCalculatedFee());
                    return m;
                }).toList();
    }

    @GetMapping("/payments")
    public List<Map<String, Object>> payments(
            @RequestParam(required = false) String period) {
        LocalDate today = LocalDate.now();
        LocalDate start = "month".equalsIgnoreCase(period) ? today.withDayOfMonth(1)
                : "week".equalsIgnoreCase(period) ? today.minusDays(6) : today;
        List<Payment> rows = paymentRepository.findByPaymentTimeBetween(start.atStartOfDay(), today.atTime(LocalTime.MAX));
        return rows.stream().filter(p -> p.getPaymentStatus() == PaymentStatus.PAID).map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            ParkingSession s = p.getParkingSession();
            m.put("id", p.getId());
            m.put("amount", p.getAmount());
            m.put("method", p.getPaymentMethod().name());
            m.put("status", p.getPaymentStatus().name());
            m.put("reference", p.getTransactionReference());
            m.put("paymentTime", p.getPaymentTime());
            m.put("registrationNumber", s.getVehicle().getRegistrationNumber());
            m.put("driverName", s.getVehicle().getDriverName());
            m.put("durationMinutes", s.getDurationMinutes());
            return m;
        }).toList();
    }

    @GetMapping("/reports/summary")
    public Map<String, Object> reportSummary() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.withDayOfMonth(1);
        List<Payment> paid = paymentRepository.findByPaymentStatus(PaymentStatus.PAID);
        BigDecimal todayTotal = BigDecimal.ZERO, weekTotal = BigDecimal.ZERO, monthTotal = BigDecimal.ZERO;
        for (Payment p : paid) {
            if (p.getPaymentTime() == null) continue;
            LocalDate d = p.getPaymentTime().toLocalDate();
            if (d.equals(today)) todayTotal = todayTotal.add(p.getAmount());
            if (!d.isBefore(weekStart)) weekTotal = weekTotal.add(p.getAmount());
            if (!d.isBefore(monthStart)) monthTotal = monthTotal.add(p.getAmount());
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayRevenue", todayTotal);
        result.put("weekRevenue", weekTotal);
        result.put("monthRevenue", monthTotal);
        result.put("paidTransactions", paid.size());
        result.put("totalVehicles", vehicleRepository.count());
        result.put("totalSlots", slotRepository.count());
        result.put("activeSessions", sessionRepository.countBySessionStatus(SessionStatus.ACTIVE));
        return result;
    }

    @GetMapping("/settings")
    public Map<String, Object> settings() {
        Map<String, Object> result = new LinkedHashMap<>();
        rateRepository.findFirstByActiveTrueOrderByEffectiveFromDesc().ifPresent(r -> {
            Map<String, Object> rate = new LinkedHashMap<>();
            rate.put("firstHourRate", r.getFirstHourRate());
            rate.put("subsequentHourRate", r.getSubsequentHourRate());
            rate.put("dayPassRate", r.getDayPassRate());
            rate.put("monthlyPassRate", r.getMonthlyPassRate());
            rate.put("gracePeriodMinutes", r.getGracePeriodMinutes());
            result.put("rate", rate);
        });
        notificationRepository.findAll().stream().findFirst().ifPresent(n -> {
            Map<String, Object> notifications = new LinkedHashMap<>();
            notifications.put("fullCapacityAlert", n.getFullCapacityAlert());
            notifications.put("highOccupancyWarning", n.getHighOccupancyWarning());
            notifications.put("dailyRevenueReport", n.getDailyRevenueReport());
            notifications.put("longDurationAlert", n.getLongDurationAlert());
            notifications.put("alertEmail", n.getAlertEmail());
            notifications.put("occupancyThreshold", n.getOccupancyThreshold());
            notifications.put("longDurationHours", n.getLongDurationHours());
            result.put("notifications", notifications);
        });
        return result;
    }

    @PutMapping("/settings/rates")
    @Transactional
    public Map<String, Object> updateRates(@RequestBody Map<String, Object> body) {
        ParkingRate rate = rateRepository.findFirstByActiveTrueOrderByEffectiveFromDesc()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No active parking rate is configured."));
        rate.setFirstHourRate(new BigDecimal(body.get("firstHourRate").toString()));
        rate.setSubsequentHourRate(new BigDecimal(body.get("subsequentHourRate").toString()));
        rate.setDayPassRate(new BigDecimal(body.get("dayPassRate").toString()));
        rate.setMonthlyPassRate(new BigDecimal(body.get("monthlyPassRate").toString()));
        rate.setGracePeriodMinutes(Integer.valueOf(body.get("gracePeriodMinutes").toString()));
        rateRepository.save(rate);
        return settings();
    }

}

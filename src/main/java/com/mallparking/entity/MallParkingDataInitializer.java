package com.mallparking.entity;

import com.mallparking.entity.enums.EventType;
import com.mallparking.entity.enums.PaymentMethod;
import com.mallparking.entity.enums.PaymentStatus;
import com.mallparking.entity.enums.SessionStatus;
import com.mallparking.entity.enums.SlotStatus;
import com.mallparking.entity.enums.VehicleType;
import com.mallparking.repository.ParkingEventRepository;
import com.mallparking.repository.ParkingFloorRepository;
import com.mallparking.repository.ParkingRateRepository;
import com.mallparking.repository.ParkingSessionRepository;
import com.mallparking.repository.ParkingSlotRepository;
import com.mallparking.repository.PaymentRepository;
import com.mallparking.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates demo data once, when the application starts with an empty database.
 *
 * Package is intentionally com.mallparking.entity so that it can use the
 * protected JPA no-argument constructors of the entity classes.
 */
@Component
public class MallParkingDataInitializer implements CommandLineRunner {

    private final ParkingFloorRepository floorRepository;
    private final ParkingSlotRepository slotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSessionRepository sessionRepository;
    private final PaymentRepository paymentRepository;
    private final ParkingEventRepository eventRepository;
    private final ParkingRateRepository rateRepository;

    public MallParkingDataInitializer(
            ParkingFloorRepository floorRepository,
            ParkingSlotRepository slotRepository,
            VehicleRepository vehicleRepository,
            ParkingSessionRepository sessionRepository,
            PaymentRepository paymentRepository,
            ParkingEventRepository eventRepository,
            ParkingRateRepository rateRepository) {
        this.floorRepository = floorRepository;
        this.slotRepository = slotRepository;
        this.vehicleRepository = vehicleRepository;
        this.sessionRepository = sessionRepository;
        this.paymentRepository = paymentRepository;
        this.eventRepository = eventRepository;
        this.rateRepository = rateRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Prevent duplicate demo records every time the application restarts.
        if (floorRepository.count() > 0) {
            System.out.println("Mall Parking demo data already exists. Skipping initialization.");
            return;
        }

        System.out.println("Initializing Mall Parking demo data...");

        List<ParkingFloor> floors = createFloors();
        floorRepository.saveAll(floors);

        List<ParkingSlot> slots = createSlots(floors);
        slotRepository.saveAll(slots);

        List<Vehicle> vehicles = createVehicles(362);
        vehicleRepository.saveAll(vehicles);

        createActiveSessions(slots, vehicles, 312);
        createCompletedSessions(slots, vehicles, 312, 50);
        createDefaultRate();

        System.out.println("Mall Parking demo data initialization completed.");
        System.out.println("Floors: 3 | Slots: 450 | Active sessions: 312 | Reserved: 5 | Available: 133");
    }

    private List<ParkingFloor> createFloors() {
        List<ParkingFloor> floors = new ArrayList<>();
        String[][] floorInfo = {
                {"B1", "Basement 1"},
                {"B2", "Basement 2"},
                {"GF", "Ground Floor"}
        };

        for (String[] info : floorInfo) {
            ParkingFloor floor = new ParkingFloor();
            floor.setFloorCode(info[0]);
            floor.setFloorName(info[1]);
            floor.setTotalCapacity(150);
            floor.setActive(true);
            floors.add(floor);
        }
        return floors;
    }

    private List<ParkingSlot> createSlots(List<ParkingFloor> floors) {
        List<ParkingSlot> slots = new ArrayList<>();
        int[] reservedPerFloor = {2, 2, 1};

        for (int floorIndex = 0; floorIndex < floors.size(); floorIndex++) {
            ParkingFloor floor = floors.get(floorIndex);

            for (int number = 1; number <= 150; number++) {
                ParkingSlot slot = new ParkingSlot();
                slot.setSlotNumber(floor.getFloorCode() + "-" + String.format("%03d", number));
                slot.setSlotRow((number - 1) / 10 + 1);
                slot.setSlotColumn((number - 1) % 10 + 1);
                slot.setSlotType("STANDARD");
                slot.setParkingFloor(floor);

                if (number <= reservedPerFloor[floorIndex]) {
                    slot.setStatus(SlotStatus.RESERVED);
                } else {
                    slot.setStatus(SlotStatus.AVAILABLE);
                }
                slots.add(slot);
            }
        }
        return slots;
    }

    private List<Vehicle> createVehicles(int count) {
        String[] names = {
                "Arun Kumar", "Priya Raman", "Karthik Raj", "Divya S",
                "Vijay Anand", "Meena Krishnan", "Rahul M", "Sneha R",
                "Sanjay Kumar", "Ananya Iyer", "Naveen B", "Harini K"
        };
        VehicleType[] types = VehicleType.values();
        List<Vehicle> vehicles = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setRegistrationNumber(String.format("TN09AB%04d", i));
            vehicle.setDriverName(names[(i - 1) % names.length] + " " + i);
            vehicle.setPhoneNumber(String.format("90000%05d", i));
            vehicle.setVehicleType(types[(i - 1) % types.length]);
            vehicles.add(vehicle);
        }
        return vehicles;
    }

    private void createActiveSessions(List<ParkingSlot> slots, List<Vehicle> vehicles, int count) {
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            ParkingSession session = new ParkingSession();
            session.setVehicle(vehicles.get(i));
            session.setParkingSlot(slots.get(i));
            session.setEntryTime(now.minusMinutes(20L + (i % 500)));
            session.setExitTime(null);
            session.setDurationMinutes(null);
            session.setCalculatedFee(BigDecimal.ZERO);
            session.setSessionStatus(SessionStatus.ACTIVE);
            sessionRepository.save(session);

            ParkingEvent event = new ParkingEvent();
            event.setEventType(EventType.ENTRY);
            event.setEventTime(session.getEntryTime());
            event.setRemarks("Demo vehicle entry");
            event.setParkingSession(session);
            eventRepository.save(event);
        }
    }

    private void createCompletedSessions(
            List<ParkingSlot> slots, List<Vehicle> vehicles, int vehicleStart, int count) {

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            LocalDateTime entry = now.minusDays(1).minusHours(i % 8).minusMinutes(15L * i);
            int duration = 60 + (i % 240);
            LocalDateTime exit = entry.plusMinutes(duration);
            BigDecimal fee = BigDecimal.valueOf(30 + Math.max(0, (duration - 60) / 60) * 20L);

            ParkingSession session = new ParkingSession();
            session.setVehicle(vehicles.get(vehicleStart + i));
            session.setParkingSlot(slots.get(i));
            session.setEntryTime(entry);
            session.setExitTime(exit);
            session.setDurationMinutes(duration);
            session.setCalculatedFee(fee);
            session.setSessionStatus(SessionStatus.COMPLETED);
            sessionRepository.save(session);

            ParkingEvent entryEvent = new ParkingEvent();
            entryEvent.setEventType(EventType.ENTRY);
            entryEvent.setEventTime(entry);
            entryEvent.setRemarks("Demo historical entry");
            entryEvent.setParkingSession(session);
            eventRepository.save(entryEvent);

            ParkingEvent exitEvent = new ParkingEvent();
            exitEvent.setEventType(EventType.EXIT);
            exitEvent.setEventTime(exit);
            exitEvent.setRemarks("Demo historical exit");
            exitEvent.setParkingSession(session);
            eventRepository.save(exitEvent);

            Payment payment = new Payment();
            payment.setAmount(fee);
            payment.setPaymentMethod(PaymentMethod.values()[i % PaymentMethod.values().length]);
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setTransactionReference(String.format("DEMO-TXN-%04d", i + 1));
            payment.setPaymentTime(exit);
            payment.setParkingSession(session);
            paymentRepository.save(payment);
        }
    }

    private void createDefaultRate() {
        ParkingRate rate = new ParkingRate();
        rate.setFirstHourRate(new BigDecimal("30.00"));
        rate.setSubsequentHourRate(new BigDecimal("20.00"));
        rate.setDayPassRate(new BigDecimal("150.00"));
        rate.setMonthlyPassRate(new BigDecimal("2500.00"));
        rate.setGracePeriodMinutes(10);
        rate.setActive(true);
        rateRepository.save(rate);
    }
}

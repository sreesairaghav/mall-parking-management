
package com.mallparking.entity;

import com.mallparking.entity.enums.SessionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_session",
    indexes = {
        @Index(name = "idx_session_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_session_slot", columnList = "slot_id"),
        @Index(name = "idx_session_status", columnList = "session_status"),
        @Index(name = "idx_session_entry", columnList = "entry_time")
    }
)
public class ParkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Min(0)
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "calculated_fee", nullable = false,
        precision = 10, scale = 2)
    private BigDecimal calculatedFee = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", nullable = false, length = 20)
    private SessionStatus sessionStatus = SessionStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false)
    private ParkingSlot parkingSlot;

    @OneToMany(mappedBy = "parkingSession", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "parkingSession", fetch = FetchType.LAZY)
    private List<ParkingEvent> parkingEvents = new ArrayList<>();

    protected ParkingSession() {
    }

    public ParkingSession(Vehicle vehicle, ParkingSlot parkingSlot,
                          LocalDateTime entryTime) {
        this.vehicle = vehicle;
        this.parkingSlot = parkingSlot;
        this.entryTime = entryTime;
        this.sessionStatus = SessionStatus.ACTIVE;
        this.calculatedFee = BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (entryTime == null) {
            entryTime = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getCalculatedFee() { return calculatedFee; }
    public void setCalculatedFee(BigDecimal calculatedFee) {
        this.calculatedFee = calculatedFee;
    }

    public SessionStatus getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public ParkingSlot getParkingSlot() { return parkingSlot; }
    public void setParkingSlot(ParkingSlot parkingSlot) {
        this.parkingSlot = parkingSlot;
    }

    public List<Payment> getPayments() { return payments; }
    public List<ParkingEvent> getParkingEvents() { return parkingEvents; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ParkingSession other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


package com.mallparking.entity;

import com.mallparking.entity.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_vehicle_registration",
        columnNames = "registration_number"
    ),
    indexes = @Index(
        name = "idx_vehicle_driver",
        columnList = "driver_name"
    )
)
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "registration_number", nullable = false,
        unique = true, length = 20)
    private String registrationNumber;

    @NotBlank
    @Size(max = 100)
    @Column(name = "driver_name", nullable = false, length = 100)
    private String driverName;

    @Size(max = 15)
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 30)
    private VehicleType vehicleType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<ParkingSession> parkingSessions = new ArrayList<>();

    protected Vehicle() {
    }

    public Vehicle(String registrationNumber, String driverName,
                   String phoneNumber, VehicleType vehicleType) {
        this.registrationNumber = registrationNumber;
        this.driverName = driverName;
        this.phoneNumber = phoneNumber;
        this.vehicleType = vehicleType;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<ParkingSession> getParkingSessions() {
        return parkingSessions;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Vehicle other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

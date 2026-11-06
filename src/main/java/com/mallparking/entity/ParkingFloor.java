
package com.mallparking.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical parking floor in the shopping mall.
 *
 * Examples:
 * B1 - Basement Floor 1
 * B2 - Basement Floor 2
 * GF - Ground Floor
 *
 * Database table: parking_floor
 *
 * Relationships:
 * One ParkingFloor -> Many ParkingSlots
 */
@Entity
@Table(name = "parking_floor")
public class ParkingFloor {

    // Primary Key

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;


    // Floor Information

    @NotBlank(message = "Floor code cannot be empty")
    @Size(max = 10, message = "Floor code cannot exceed 10 characters")
    @Column(name = "floor_code", nullable = false, unique = true, length = 10)
    private String floorCode;

    @NotBlank(message = "Floor name cannot be empty")
    @Size(max = 100, message = "Floor name cannot exceed 100 characters")
    @Column(name = "floor_name", nullable = false, length = 100)
    private String floorName;

    @NotNull(message = "Total capacity is required")
    @Min(value = 1, message = "Floor capacity must be at least 1")
    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean active = Boolean.TRUE;


    // Timestamps

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // Relationships

    /**
     * One floor can contain multiple parking slots.
     *
     * The foreign key floor_id is maintained
     * in the ParkingSlot entity.
     */
    @OneToMany(
            mappedBy = "parkingFloor",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            fetch = FetchType.LAZY
    )
    private List<ParkingSlot> slots = new ArrayList<>();


    // Constructors

    /**
     * Required by JPA.
     */
    protected ParkingFloor() {
    }

    /**
     * Constructor for creating a new parking floor.
     */
    public ParkingFloor(
            String floorCode,
            String floorName,
            Integer totalCapacity
    ) {
        this.floorCode = floorCode;
        this.floorName = floorName;
        this.totalCapacity = totalCapacity;
        this.active = Boolean.TRUE;
    }


    // Lifecycle Callbacks

    /**
     * Automatically initializes timestamps before insertion.
     */
    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (this.createdAt == null) {
            this.createdAt = now;
        }

        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    /**
     * Automatically updates updatedAt before modification.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    // Relationship Helper Methods

    /**
     * Adds a parking slot to this floor.
     */
    public void addSlot(ParkingSlot slot) {

        if (slot == null) {
            throw new IllegalArgumentException(
                    "Parking slot cannot be null"
            );
        }

        slots.add(slot);
        slot.setParkingFloor(this);
    }

    /**
     * Removes a parking slot from this floor.
     */
    public void removeSlot(ParkingSlot slot) {

        if (slots.remove(slot)) {
            slot.setParkingFloor(null);
        }
    }


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getFloorCode() {
        return floorCode;
    }

    public void setFloorCode(String floorCode) {
        this.floorCode = floorCode;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }


    // Object Methods

    @Override
    public String toString() {

        return "ParkingFloor{" +
                "id=" + id +
                ", floorCode='" + floorCode + '\'' +
                ", floorName='" + floorName + '\'' +
                ", totalCapacity=" + totalCapacity +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (!(object instanceof ParkingFloor other)) {
            return false;
        }

        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

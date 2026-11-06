
package com.mallparking.entity;

import com.mallparking.entity.enums.SlotStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_slot",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_floor_slot_number",
        columnNames = {"floor_id", "slot_number"}
    ),
    indexes = {
        @Index(name = "idx_slot_floor", columnList = "floor_id"),
        @Index(name = "idx_slot_status", columnList = "status")
    }
)
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "slot_number", nullable = false, length = 20)
    private String slotNumber;

    @NotNull
    @Min(1)
    @Column(name = "slot_row", nullable = false)
    private Integer slotRow;

    @NotNull
    @Min(1)
    @Column(name = "slot_column", nullable = false)
    private Integer slotColumn;

    @NotBlank
    @Size(max = 30)
    @Column(name = "slot_type", nullable = false, length = 30)
    private String slotType = "STANDARD";

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "floor_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_slot_floor"))
    private ParkingFloor parkingFloor;

    @OneToMany(mappedBy = "parkingSlot", fetch = FetchType.LAZY)
    private List<ParkingSession> parkingSessions = new ArrayList<>();

    protected ParkingSlot() {
    }

    public ParkingSlot(String slotNumber, Integer slotRow,
                       Integer slotColumn, String slotType) {
        this.slotNumber = slotNumber;
        this.slotRow = slotRow;
        this.slotColumn = slotColumn;
        this.slotType = slotType;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public Integer getSlotRow() { return slotRow; }
    public void setSlotRow(Integer slotRow) { this.slotRow = slotRow; }

    public Integer getSlotColumn() { return slotColumn; }
    public void setSlotColumn(Integer slotColumn) { this.slotColumn = slotColumn; }

    public String getSlotType() { return slotType; }
    public void setSlotType(String slotType) { this.slotType = slotType; }

    public SlotStatus getStatus() { return status; }
    public void setStatus(SlotStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public ParkingFloor getParkingFloor() { return parkingFloor; }
    public void setParkingFloor(ParkingFloor parkingFloor) {
        this.parkingFloor = parkingFloor;
    }

    public List<ParkingSession> getParkingSessions() {
        return parkingSessions;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ParkingSlot other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

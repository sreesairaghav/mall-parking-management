
package com.mallparking.entity;

import com.mallparking.entity.enums.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_event",
    indexes = {
        @Index(name = "idx_event_session", columnList = "session_id"),
        @Index(name = "idx_event_time", columnList = "event_time"),
        @Index(name = "idx_event_type", columnList = "event_type")
    }
)
public class ParkingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    @NotNull
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Size(max = 255)
    @Column(name = "remarks", length = 255)
    private String remarks;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_event_session"))
    private ParkingSession parkingSession;

    /**
     * Optional administrator who recorded the event.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id",
        foreignKey = @ForeignKey(name = "fk_event_admin"))
    private Admin recordedBy;

    protected ParkingEvent() {
    }

    public ParkingEvent(EventType eventType,
                        ParkingSession parkingSession,
                        String remarks) {
        this.eventType = eventType;
        this.parkingSession = parkingSession;
        this.remarks = remarks;
    }

    @PrePersist
    protected void onCreate() {
        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public ParkingSession getParkingSession() { return parkingSession; }
    public void setParkingSession(ParkingSession parkingSession) {
        this.parkingSession = parkingSession;
    }

    public Admin getRecordedBy() { return recordedBy; }
    public void setRecordedBy(Admin recordedBy) {
        this.recordedBy = recordedBy;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ParkingEvent other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

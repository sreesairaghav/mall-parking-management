
package com.mallparking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "notification_setting")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "full_capacity_alert", nullable = false)
    private Boolean fullCapacityAlert = true;

    @NotNull
    @Column(name = "high_occupancy_warning", nullable = false)
    private Boolean highOccupancyWarning = true;

    @NotNull
    @Column(name = "daily_revenue_report", nullable = false)
    private Boolean dailyRevenueReport = true;

    @NotNull
    @Column(name = "long_duration_alert", nullable = false)
    private Boolean longDurationAlert = false;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(name = "alert_email", nullable = false, length = 150)
    private String alertEmail = "admin@mallparking.com";

    @NotNull
    @Min(1)
    @Max(100)
    @Column(name = "occupancy_threshold", nullable = false)
    private Integer occupancyThreshold = 80;

    @NotNull
    @Min(1)
    @Column(name = "long_duration_hours", nullable = false)
    private Integer longDurationHours = 6;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected NotificationSetting() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Boolean getFullCapacityAlert() { return fullCapacityAlert; }
    public void setFullCapacityAlert(Boolean value) {
        fullCapacityAlert = value;
    }

    public Boolean getHighOccupancyWarning() { return highOccupancyWarning; }
    public void setHighOccupancyWarning(Boolean value) {
        highOccupancyWarning = value;
    }

    public Boolean getDailyRevenueReport() { return dailyRevenueReport; }
    public void setDailyRevenueReport(Boolean value) {
        dailyRevenueReport = value;
    }

    public Boolean getLongDurationAlert() { return longDurationAlert; }
    public void setLongDurationAlert(Boolean value) {
        longDurationAlert = value;
    }

    public String getAlertEmail() { return alertEmail; }
    public void setAlertEmail(String alertEmail) {
        this.alertEmail = alertEmail;
    }

    public Integer getOccupancyThreshold() { return occupancyThreshold; }
    public void setOccupancyThreshold(Integer value) {
        occupancyThreshold = value;
    }

    public Integer getLongDurationHours() { return longDurationHours; }
    public void setLongDurationHours(Integer value) {
        longDurationHours = value;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof NotificationSetting other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}


package com.mallparking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "parking_rate",
    indexes = @Index(name = "idx_rate_active", columnList = "is_active")
)
public class ParkingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "first_hour_rate", nullable = false,
        precision = 10, scale = 2)
    private BigDecimal firstHourRate = new BigDecimal("30.00");

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "subsequent_hour_rate", nullable = false,
        precision = 10, scale = 2)
    private BigDecimal subsequentHourRate = new BigDecimal("20.00");

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "day_pass_rate", nullable = false,
        precision = 10, scale = 2)
    private BigDecimal dayPassRate = new BigDecimal("150.00");

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "monthly_pass_rate", nullable = false,
        precision = 10, scale = 2)
    private BigDecimal monthlyPassRate = new BigDecimal("2500.00");

    @NotNull
    @Min(0)
    @Column(name = "grace_period_minutes", nullable = false)
    private Integer gracePeriodMinutes = 10;

    @NotNull
    @Column(name = "effective_from", nullable = false)
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_until")
    private LocalDateTime effectiveUntil;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ParkingRate() {
    }

    public ParkingRate(BigDecimal firstHourRate,
                       BigDecimal subsequentHourRate,
                       BigDecimal dayPassRate,
                       BigDecimal monthlyPassRate,
                       Integer gracePeriodMinutes) {
        this.firstHourRate = firstHourRate;
        this.subsequentHourRate = subsequentHourRate;
        this.dayPassRate = dayPassRate;
        this.monthlyPassRate = monthlyPassRate;
        this.gracePeriodMinutes = gracePeriodMinutes;
        this.active = Boolean.TRUE;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) createdAt = now;
        if (effectiveFrom == null) effectiveFrom = now;
    }

    public Long getId() { return id; }

    public BigDecimal getFirstHourRate() { return firstHourRate; }
    public void setFirstHourRate(BigDecimal value) { firstHourRate = value; }

    public BigDecimal getSubsequentHourRate() { return subsequentHourRate; }
    public void setSubsequentHourRate(BigDecimal value) {
        subsequentHourRate = value;
    }

    public BigDecimal getDayPassRate() { return dayPassRate; }
    public void setDayPassRate(BigDecimal value) { dayPassRate = value; }

    public BigDecimal getMonthlyPassRate() { return monthlyPassRate; }
    public void setMonthlyPassRate(BigDecimal value) { monthlyPassRate = value; }

    public Integer getGracePeriodMinutes() { return gracePeriodMinutes; }
    public void setGracePeriodMinutes(Integer value) {
        gracePeriodMinutes = value;
    }

    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime value) {
        effectiveFrom = value;
    }

    public LocalDateTime getEffectiveUntil() { return effectiveUntil; }
    public void setEffectiveUntil(LocalDateTime value) {
        effectiveUntil = value;
    }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ParkingRate other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}

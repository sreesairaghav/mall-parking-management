package com.mallparking.entity.enums;

/**
 * Operational status of a {@link com.mallparking.entity.ParkingSlot}.
 *
 * <p><strong>Important design note:</strong> {@code OCCUPIED} is intentionally
 * absent here. Whether a slot is occupied is derived at query time from the
 * existence of an {@link com.mallparking.entity.ParkingSession} whose
 * {@code sessionStatus} is {@code ACTIVE} and whose {@code parkingSlot} points
 * to that slot. This prevents the two-status inconsistency that can arise when
 * a slot row says OCCUPIED but no session record backs it up.</p>
 *
 * <ul>
 *   <li>{@code AVAILABLE}    – slot is open and can accept a vehicle.</li>
 *   <li>{@code RESERVED}     – slot is pre-booked; not yet occupied.</li>
 *   <li>{@code MAINTENANCE}  – slot is out of service (cleaning, repair, etc.).</li>
 * </ul>
 */
public enum SlotStatus {

    /** Slot is free and can be assigned to an incoming vehicle. */
    AVAILABLE,

    /** Slot is pre-booked for a specific vehicle or time window. */
    RESERVED,

    /** Slot is temporarily or permanently out of service. */
    MAINTENANCE
}

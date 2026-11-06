package com.mallparking.entity.enums;

/**
 * Lifecycle status of a {@link com.mallparking.entity.ParkingSession}.
 *
 * <ul>
 *   <li>{@code ACTIVE}    – vehicle is currently parked; exit has not been recorded.</li>
 *   <li>{@code COMPLETED} – vehicle has exited and the session has been closed
 *                           (payment may still be in a {@code PENDING} state if
 *                           payment processing is deferred).</li>
 * </ul>
 */
public enum SessionStatus {

    /**
     * Session is ongoing. The associated {@link com.mallparking.entity.ParkingSlot}
     * is considered occupied for the duration of an ACTIVE session.
     */
    ACTIVE,

    /**
     * Session is closed. Exit time has been recorded and the slot is free to be
     * assigned to the next vehicle (subject to its {@code slotStatus}).
     */
    COMPLETED
}

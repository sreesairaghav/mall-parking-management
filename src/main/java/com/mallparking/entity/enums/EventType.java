package com.mallparking.entity.enums;

/**
 * Type of event recorded in a {@link com.mallparking.entity.ParkingEvent}.
 *
 * <ul>
 *   <li>{@code ENTRY} – vehicle drove into the parking slot; session begins.</li>
 *   <li>{@code EXIT}  – vehicle drove out; session may transition to COMPLETED.</li>
 * </ul>
 */
public enum EventType {

    /** Vehicle entry event — recorded when a vehicle enters a parking slot. */
    ENTRY,

    /** Vehicle exit event — recorded when a vehicle leaves a parking slot. */
    EXIT
}

package com.mallparking.entity.enums;

/**
 * Category of a vehicle that can be parked in the mall.
 *
 * <ul>
 *   <li>{@code CAR}         – standard passenger car.</li>
 *   <li>{@code SUV}         – sport-utility vehicle / MPV.</li>
 *   <li>{@code MOTORCYCLE}  – two-wheelers (bikes, scooters).</li>
 *   <li>{@code EV}          – electric vehicle (may be assigned to slots with
 *                             charging points in a future phase).</li>
 * </ul>
 */
public enum VehicleType {

    /** Standard passenger car. */
    CAR,

    /** Sport-utility vehicle or multi-purpose vehicle. */
    SUV,

    /** Two-wheeler (motorcycle / scooter). */
    MOTORCYCLE,

    /** Battery-electric vehicle, optionally assigned to EV-charging slots. */
    EV
}

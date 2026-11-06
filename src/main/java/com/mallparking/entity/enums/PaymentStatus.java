package com.mallparking.entity.enums;

/**
 * Lifecycle status of a {@link com.mallparking.entity.Payment} record.
 *
 * <ul>
 *   <li>{@code PENDING}  – payment has been initiated but not yet confirmed by
 *                          the payment gateway or cashier.</li>
 *   <li>{@code PAID}     – payment completed successfully.</li>
 *   <li>{@code FAILED}   – payment attempt was rejected or timed out; a new
 *                          Payment record should be created for the next attempt.</li>
 *   <li>{@code REFUNDED} – a previously successful payment has been reversed.</li>
 * </ul>
 */
public enum PaymentStatus {

    /** Payment attempt is in progress. */
    PENDING,

    /** Payment confirmed and settled. */
    PAID,

    /** Payment attempt did not succeed. */
    FAILED,

    /** Successfully collected payment has been reversed. */
    REFUNDED
}

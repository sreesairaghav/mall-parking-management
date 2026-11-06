package com.mallparking.entity.enums;

/**
 * Payment channel used to settle a {@link com.mallparking.entity.Payment}.
 *
 * <ul>
 *   <li>{@code UPI}  – Unified Payments Interface (PhonePe, GPay, Paytm, etc.).</li>
 *   <li>{@code CASH} – physical cash collected at the payment counter.</li>
 *   <li>{@code CARD} – debit or credit card via POS terminal.</li>
 * </ul>
 */
public enum PaymentMethod {

    /** UPI-based digital payment. */
    UPI,

    /** Cash payment collected at the counter. */
    CASH,

    /** Debit or credit card via POS terminal. */
    CARD
}

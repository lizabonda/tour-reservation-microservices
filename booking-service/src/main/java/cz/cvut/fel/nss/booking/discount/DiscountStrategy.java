package cz.cvut.fel.nss.booking.discount;

import java.time.LocalDate;

/**
 * Strategy interface for booking discounts.
 * Implementations decide whether they apply to a booking and calculate the discount amount.
 */
public interface DiscountStrategy {
    /**
     * Checks whether this discount can be used for the given dates.
     *
     * @param createdAt date when the booking is created
     * @param tourStartDate date when the tour starts
     * @return {@code true} when this strategy applies
     */
    boolean supports(LocalDate createdAt, LocalDate tourStartDate);

    /**
     * Calculates the discount amount.
     *
     * @param basePrice price before discounts
     * @return discount amount
     */
    double calculateDiscount(double basePrice);

    /**
     * Explains how the discount was calculated.
     *
     * @param createdAt date when the booking is created
     * @param tourStartDate date when the tour starts
     * @param basePrice price before discounts
     * @return human-readable discount explanation
     */
    String explain(LocalDate createdAt, LocalDate tourStartDate, double basePrice);
}

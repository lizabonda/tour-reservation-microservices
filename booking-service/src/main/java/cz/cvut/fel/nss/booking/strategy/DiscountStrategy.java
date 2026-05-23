package cz.cvut.fel.nss.booking.strategy;

import java.time.LocalDate;

public interface DiscountStrategy {
    boolean supports(LocalDate createdAt, LocalDate tourStartDate);
    double calculateDiscount(double basePrice);
    String explain(LocalDate createdAt, LocalDate tourStartDate, double basePrice);
}

package cz.cvut.fel.nss.booking.discount;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@Order(1)
public class EarlyBookingDiscountStrategy implements DiscountStrategy {
    private static final int EARLY_DAYS = 45;
    private static final double EARLY_DAYS_DISCOUNT = 0.1;

    @Override
    public boolean supports(LocalDate createdAt, LocalDate tourStartDate) {
        return ChronoUnit.DAYS.between(createdAt, tourStartDate) >= EARLY_DAYS;
    }

    @Override
    public double calculateDiscount(double basePrice) {
        return basePrice * EARLY_DAYS_DISCOUNT;
    }

    @Override
    public String explain(LocalDate createdAt, LocalDate tourStartDate, double basePrice) {
        long days = ChronoUnit.DAYS.between(createdAt, tourStartDate);
        return String.format(
                "Early booking discount: days=%d >= %d -> discount = base(%.2f) * %.2f = %.2f",
                days, EARLY_DAYS, basePrice, EARLY_DAYS_DISCOUNT, calculateDiscount(basePrice)
        );
    }
}

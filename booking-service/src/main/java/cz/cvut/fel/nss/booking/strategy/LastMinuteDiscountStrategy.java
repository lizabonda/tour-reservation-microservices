package cz.cvut.fel.nss.booking.strategy;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@Order(1)
public class LastMinuteDiscountStrategy implements DiscountStrategy {
    private static final int LASTMINUTE_DAYS = 14;
    private static final double LASTMINUTE_DAYS_DISCOUNT = 0.15;

    @Override
    public boolean supports(LocalDate createdAt, LocalDate tourStartDate) {
        return ChronoUnit.DAYS.between(createdAt, tourStartDate) <= LASTMINUTE_DAYS;
    }

    @Override
    public double calculateDiscount(double basePrice) {
        return basePrice * LASTMINUTE_DAYS_DISCOUNT;
    }

    @Override
    public String explain(LocalDate createdAt, LocalDate tourStartDate, double basePrice) {
        long days = ChronoUnit.DAYS.between(createdAt, tourStartDate);
        return String.format(
                "Last-minute discount: days=%d <= %d -> discount = base(%.2f) * %.2f = %.2f",
                days, LASTMINUTE_DAYS, basePrice, LASTMINUTE_DAYS_DISCOUNT, calculateDiscount(basePrice)
        );
    }
}

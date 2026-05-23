package cz.cvut.fel.nss.booking.strategy;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class NoDiscountStrategy implements DiscountStrategy {
    private static final int EARLY_DAYS = 45;
    private static final int LASTMINUTE_DAYS = 14;

    @Override
    public boolean supports(LocalDate createdAt, LocalDate tourStartDate) {
        return true;
    }

    @Override
    public double calculateDiscount(double basePrice) {
        return 0.0;
    }

    @Override
    public String explain(LocalDate createdAt, LocalDate tourStartDate, double basePrice) {
        long days = ChronoUnit.DAYS.between(createdAt, tourStartDate);
        return String.format(
                "No discount applied: days=%d is between %d and %d -> discount = 0.00",
                days, LASTMINUTE_DAYS, EARLY_DAYS
        );
    }
}

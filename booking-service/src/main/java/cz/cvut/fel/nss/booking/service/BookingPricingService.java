package cz.cvut.fel.nss.booking.service;

import cz.cvut.fel.nss.booking.discount.DiscountStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingPricingService {

    private final List<DiscountStrategy> discountStrategies;

    public BookingPricingService(List<DiscountStrategy> discountStrategies) {
        this.discountStrategies = discountStrategies;
    }

    public double discount(LocalDate createdAt, LocalDate tourStartDate, double basePrice) {
        DiscountStrategy strategy = resolveStrategy(createdAt, tourStartDate);
        return strategy.calculateDiscount(basePrice);
    }

    public double totalPrice(LocalDate createdAt,
                             LocalDate tourStartDate,
                             double tourPrice,
                             double accommodationPrice,
                             double allInclusiveCharge) {
        double basePrice = tourPrice + accommodationPrice;
        double discount = discount(createdAt, tourStartDate, basePrice);

        return basePrice - discount + allInclusiveCharge;
    }

    public String priceReport(Long bookingId,
                              LocalDate createdAt,
                              LocalDate tourStartDate,
                              double tourPrice,
                              double accommodationPrice,
                              double allInclusiveCharge) {
        double basePrice = tourPrice + accommodationPrice;
        DiscountStrategy strategy = resolveStrategy(createdAt, tourStartDate);

        double discount = strategy.calculateDiscount(basePrice);
        double total = basePrice - discount + allInclusiveCharge;
        long days = ChronoUnit.DAYS.between(createdAt, tourStartDate);

        String discountExplain = strategy.explain(createdAt, tourStartDate, basePrice);

        return String.format("""
                -- Price breakdown for booking %s --
                Tour price:                  %.2f
                Accommodation price:         %.2f
                Base price:                  base = tour + accommodation = %.2f = %.2f + %.2f
                Days before tour start:      %d
                %s
                Extra charges (ALL_INCLUSIVE): %.2f
                Final price:                 total = base - discount + charge
                                            = %.2f - %.2f + %.2f = %.2f
                """,
                bookingId,
                tourPrice,
                accommodationPrice,
                basePrice, tourPrice, accommodationPrice,
                days,
                discountExplain,
                allInclusiveCharge,
                basePrice, discount, allInclusiveCharge, total
        );
    }

    private DiscountStrategy resolveStrategy(LocalDate createdAt, LocalDate tourStartDate) {
        return discountStrategies.stream()
                .filter(strategy -> strategy.supports(createdAt, tourStartDate))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No discount strategy found"));
    }
}

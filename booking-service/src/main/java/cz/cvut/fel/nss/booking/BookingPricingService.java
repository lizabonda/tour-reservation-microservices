package cz.cvut.fel.nss.booking;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class BookingPricingService {
    private static final int EARLY_DAYS = 45;
    private static final int LASTMINUTE_DAYS = 14;
    private static final double LASTMINUTE_DAYS_DISCOUNT = 0.15;
    private static final double EARLY_DAYS_DISCOUNT = 0.1;


    public double discount(LocalDate createdAt, LocalDate tourStartDate, double basePrice) {
        long days = ChronoUnit.DAYS.between(createdAt, tourStartDate);
        double discount = 0.0;
        if (days >= EARLY_DAYS) {
            double erly_discount = basePrice * EARLY_DAYS_DISCOUNT;
            discount = erly_discount;
        } else if (days <= LASTMINUTE_DAYS) {
            double last_minute_discount = basePrice * LASTMINUTE_DAYS_DISCOUNT;
            discount = last_minute_discount;
        }
        return discount;
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
        double discount = discount(createdAt, tourStartDate, basePrice);
        double total = totalPrice(createdAt, tourStartDate, tourPrice, accommodationPrice, allInclusiveCharge);

        long days = ChronoUnit.DAYS.between(createdAt, tourStartDate);

        String discountExplain;
        if (days >= EARLY_DAYS) {
            discountExplain = String.format(
                    "Early booking discount: days=%d >= %d -> discount = base(%.2f) * %.2f = %.2f",
                    days, EARLY_DAYS, basePrice, EARLY_DAYS_DISCOUNT, discount
            );
        } else if (days <= LASTMINUTE_DAYS) {
            discountExplain = String.format(
                    "Last-minute discount: days=%d <= %d -> discount = base(%.2f) * %.2f = %.2f",
                    days, LASTMINUTE_DAYS, basePrice, LASTMINUTE_DAYS_DISCOUNT, discount
            );
        } else {
            discountExplain = String.format(
                    "No discount applied: days=%d is between %d and %d -> discount = 0.00",
                    days, LASTMINUTE_DAYS, EARLY_DAYS
            );
        }

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
}

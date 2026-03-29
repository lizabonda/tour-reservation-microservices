package cz.cvut.fel.nss.projekt.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {


    @Test
    void totalPrice_ShouldBeZeroByDefault() {
        Booking booking = new Booking();
        assertEquals(0.0, booking.getTotalPrice());
    }

    private Booking prepareBookingWithDaysBefore(int daysBeforeStart) {
        Tour tour = new Tour();
        tour.setTitle("Test Tour");
        tour.setPrice(1000.0);
        tour.setStartDate(LocalDate.now().plusDays(daysBeforeStart));

        Booking booking = new Booking();
        booking.setTour(tour);
        booking.setCreatedAt(LocalDate.now());
        booking.setTotalPrice(1000.0);
        booking.setReservations(List.of());
        return booking;
    }

    @Test
    void priceReport_ShouldApplyEarlyBookingDiscount_WhenDaysGreaterOrEqualTo45() {
        Booking booking = prepareBookingWithDaysBefore(50);
        String report = booking.priceReport();
        assertTrue(report.contains("Early booking discount"),"Report should mention early booking discount");
        assertTrue(report.contains("0.10"),"Discount percentage should be 0.10 for early booking");
        assertTrue(report.contains(">= 45"),"Report should compare days with EARLY_DAYS value");
    }

    @Test
    void priceReport_ShouldApplyLastMinuteDiscount_WhenDaysLessOrEqualTo3() {
        Booking booking = prepareBookingWithDaysBefore(2);
        String report = booking.priceReport();

        assertTrue(report.contains("Last-minute discount"),"Report should mention last-minute discount");
        assertTrue(report.contains("0.15"),"Discount percentage should be 0.15 for last-minute booking");
        assertTrue(report.contains("<= 14"),"Report should compare days with LASTMINUTE_DAYS value");
    }

    @Test
    void priceReport_ShouldApplyNoDiscount_WhenDaysBetween3And45() {
        Booking booking = prepareBookingWithDaysBefore(20);
        String report = booking.priceReport();

        assertTrue(report.contains("No discount applied"),"Report should mention that no discount is applied");
        assertTrue(report.contains("between 14 and 45"),"Report should show the range for no discount");
        assertTrue(report.contains("discount = 0.00"),"Report should show zero discount");
    }


}

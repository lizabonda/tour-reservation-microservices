package cz.cvut.fel.nss.projekt.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void validateAdult_ShouldThrowException_WhenBirthDateIsNull() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, user::validateAdult);
    }

    @Test
    void validateAdult_ShouldThrowException_WhenUserIsUnder18() {
        User user = new User();
        user.setDateOfBirth(LocalDate.now().minusYears(10));
        IllegalStateException ex = assertThrows(IllegalStateException.class, user::validateAdult);
        assertTrue(ex.getMessage().contains("18"));
    }

    @Test
    void validateAdult_ShouldNotThrow_WhenUserIsAdult() {
        User user = new User();
        user.setDateOfBirth(LocalDate.now().minusYears(25));
        assertDoesNotThrow(user::validateAdult);
    }

    @Test
    void toString_ShouldContainUsernameAndRole() {
        User user = new User();
        user.setUsername("alice");
        user.setRole(Role.CUSTOMER);

        String output = user.toString();

        assertTrue(output.contains("alice"));
        assertTrue(output.contains("CUSTOMER"));
    }

}

package com.example.demo.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void teardown() {
        factory.close();
    }

    private Reservation createValid() {
        Reservation r = new Reservation();
        r.setResource(new Resource());
        r.setUser(new User());
        r.setStartTime(LocalDateTime.now().plusHours(1));
        r.setEndTime(LocalDateTime.now().plusHours(2));
        r.setPrice(BigDecimal.valueOf(100));
        return r;
    }

    @Test
    void validReservationHasNoViolations() {
        Reservation r = createValid();
        Set<ConstraintViolation<Reservation>> violations = validator.validate(r);
        assertThat(violations).isEmpty();
    }

    @Test
    void endBeforeStartViolatesAssertTrue() {
        Reservation r = createValid();
        r.setEndTime(r.getStartTime().minusMinutes(10));
        Set<ConstraintViolation<Reservation>> violations = validator.validate(r);
        assertThat(violations).isNotEmpty();
        boolean found = violations.stream().anyMatch(v -> v.getMessage().contains("endTime must be after startTime"));
        assertThat(found).isTrue();
    }

    @Test
    void nullFieldsViolateNotNull() {
        Reservation r = new Reservation();
        Set<ConstraintViolation<Reservation>> violations = validator.validate(r);
        assertThat(violations).isNotEmpty();
        // should contain NotNull violations for resource, user, startTime, endTime, price
        assertThat(violations.stream().map(ConstraintViolation::getPropertyPath).map(Object::toString))
                .contains("resource", "user", "startTime", "endTime", "price");
    }

    @Test
    void nullPriceViolates() {
        Reservation r = createValid();
        r.setPrice(null);
        Set<ConstraintViolation<Reservation>> violations = validator.validate(r);
        assertThat(violations).isNotEmpty();
        boolean found = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price"));
        assertThat(found).isTrue();
    }
}

package ru.yandex.practicum.filmorate;

import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validation.ReleaseDateAfter;
import ru.yandex.practicum.filmorate.validation.ReleaseDateAfterValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReleaseDateAfterValidatorTest {
    private ReleaseDateAfterValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ReleaseDateAfterValidator();
        validator.initialize(annotation("1895-12-28"));
    }

    @Test
    void shouldAllowDateAfterThreshold() {
        assertTrue(validator.isValid(LocalDate.of(1896, 1, 1), null));
    }

    @Test
    void shouldAllowDateOnThreshold() {
        assertTrue(validator.isValid(LocalDate.of(1895, 12, 28), null));
    }

    @Test
    void shouldRejectDateBeforeThreshold() {
        assertFalse(validator.isValid(LocalDate.of(1895, 12, 27), null));
    }

    @Test
    void shouldAllowNull() {
        assertTrue(validator.isValid(null, null));
    }

    private ReleaseDateAfter annotation(String value) {
        return new ReleaseDateAfter() {
            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public String message() {
                return "Release date too early";
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return ReleaseDateAfter.class;
            }

            @Override
            public String value() {
                return value;
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }
        };
    }
}

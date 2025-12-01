package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldNotValidateNullName() {
        Film film = Film.builder()
                .name(null)
                .description("cool film")
                .releaseDate(LocalDate.of(1999, 1, 1))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldNotValidateEmptyName() {
        Film film = Film.builder()
                .name("")
                .description("cool film")
                .releaseDate(LocalDate.of(1999, 1, 1))
                .duration(120)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldNotValidateDescriptionTooLong() {
        String longDescription = "a".repeat(201);
        Film film = Film.builder()
                .name("Interstellar")
                .description(longDescription)
                .releaseDate(LocalDate.of(2014, 1, 1))
                .duration(169)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldValidateDescriptionMaxLength200() {
        String validDescription = "a".repeat(200);
        Film film = Film.builder()
                .name("Inception")
                .description(validDescription)
                .releaseDate(LocalDate.of(2010, 1, 1))
                .duration(148)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldNotValidateReleaseDateBefore18951228() {
        Film film = Film.builder()
                .name("Old Movie")
                .description("very old")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(10)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldValidateReleaseDateOn18951228() {
        Film film = Film.builder()
                .name("First Movie")
                .description("historical")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldNotValidateNegativeDuration() {
        Film film = Film.builder()
                .name("Short")
                .description("negative")
                .releaseDate(LocalDate.now())
                .duration(-5)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldNotValidateZeroDuration() {
        Film film = Film.builder()
                .name("Short")
                .description("zero")
                .releaseDate(LocalDate.now())
                .duration(0)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldValidateCorrectFilm() {
        Film film = Film.builder()
                .name("Matrix")
                .description("A computer hacker learns...")
                .releaseDate(LocalDate.of(1999, 3, 31))
                .duration(136)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }
}
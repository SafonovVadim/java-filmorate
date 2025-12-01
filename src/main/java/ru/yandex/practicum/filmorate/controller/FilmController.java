package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.utils.UtilMethods.getNextId;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(getNextId(films));
        try {
            films.put(film.getId(), film);
            log.info("Создан фильм с id={}", film.getId());
        } catch (ConstraintViolationException e) {
            log.error("Ошибка валидации");
            throw new ValidationException("Ошибка валидации");
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody(required = false) Film film) {
        if (!films.containsKey(film.getId()) || film.getId() == null) {
            log.error("Фильм с id {} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }
        try {
            films.put(film.getId(), film);
            log.info("Обновлён фильм с id={}", film.getId());
        } catch (ConstraintViolationException e) {
            log.error("Ошибка валидации");
            throw new ValidationException("Ошибка валидации");
        }
        return film;
    }
}

package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.utils.UtilMethods.getNextId;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId(films));
        films.put(film.getId(), film);
        log.info("Создан фильм с id={}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            log.error("Попытка обновить null фильм");
            throw new NotFoundException("Фильм не может быть null");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм с id={}", film.getId());
        return film;
    }

    @Override
    public Film deleteFilm(Long filmId) {
        Film film = getFilmById(filmId);
        films.remove(film.getId());
        log.info("Удален фильм с id={}", filmId);
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    public Film addLike(Long filmId, Long userId, InMemoryUserStorage userStorage) {
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        Set<Long> likes = film.getLikes() != null ? new HashSet<>(film.getLikes()) : new HashSet<>();
        likes.add(user.getId());
        film.setLikes(likes);
        updateFilm(film);
        return film;
    }

    public Film removeLike(Long filmId, Long userId, InMemoryUserStorage userStorage) {
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        Set<Long> likes = film.getLikes() != null ? new HashSet<>(film.getLikes()) : new HashSet<>();
        likes.remove(user.getId());
        film.setLikes(likes);
        updateFilm(film);
        return film;
    }

    public Film getFilmById(Long filmId) {
        if (!films.containsKey(filmId) || filmId == 0) {
            log.error("Фильм с id {} не найден", filmId);
            throw new NotFoundException("Фильм не найден");
        }
        return films.get(filmId);
    }

    public List<Film> getPopularFilms(Long count) {
        return films.values().stream()
                .filter(film -> film.getLikes() != null && !film.getLikes().isEmpty())
                .sorted(Comparator.comparing(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}

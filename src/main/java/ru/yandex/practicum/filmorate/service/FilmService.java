package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;

@Service

public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film createFilm(final Film film) {
        return inMemoryFilmStorage.createFilm(film);
    }

    public Film updateFilm(final Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public Film deleteFilm(final Long filmId) {
        return inMemoryFilmStorage.deleteFilm(filmId);
    }

    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.getAll();
    }

    public List<Film> getPopularFilms(Long count) {
        return inMemoryFilmStorage.getPopularFilms(count);
    }

    public Film addLike(final Long filmId, final Long userId) {
        return inMemoryFilmStorage.addLike(filmId, userId, inMemoryUserStorage);
    }

    public Film removeLike(final Long filmId, final Long userId) {
        return inMemoryFilmStorage.removeLike(filmId, userId, inMemoryUserStorage);
    }

    void getAllLikes() {
    }
}

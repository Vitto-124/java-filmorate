package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }
        getFilmOrThrow(film.getId());
        validateFilm(film);
        return filmStorage.update(film);
    }

    public Film findById(long id) {
        return getFilmOrThrow(id);
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        film.getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Параметр count должен быть положительным числом");
        }
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
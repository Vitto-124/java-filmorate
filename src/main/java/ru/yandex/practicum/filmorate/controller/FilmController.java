package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();
    private long globalId = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Ошибка обновления фильма: не указан id");
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.error("Ошибка обновления фильма: фильм с id={} не найден", newFilm.getId());
            throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        validate(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Обновлен фильм: id={}, name={}", newFilm.getId(), newFilm.getName());
        return newFilm;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации фильма: пустое название");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Ошибка валидации фильма: длинное описание ({})", film.getDescription().length());
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Ошибка валидации фильма: дата релиза {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.error("Ошибка валидации фильма: некорректная продолжительность {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private long getNextId() {
        return ++globalId;
    }
}
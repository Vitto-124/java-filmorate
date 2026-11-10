package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long globalId = 0;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Ошибка обновления пользователя: не указан id");
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if (!users.containsKey(newUser.getId())) {
            log.error("Ошибка обновления пользователя: пользователь с id={} не найден", newUser.getId());
            throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
        }
        validate(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Обновлен пользователь: id={}, login={}", newUser.getId(), newUser.getLogin());
        return newUser;
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации пользователя: некорректный email {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации пользователя: некорректный login {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации пользователя: дата рождения в будущем {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private long getNextId() {
        return ++globalId;
    }
}
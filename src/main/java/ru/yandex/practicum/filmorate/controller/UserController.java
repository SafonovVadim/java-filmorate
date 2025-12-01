package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.utils.UtilMethods.getNextId;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        try {
            user.setId(getNextId(users));
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Создан пользователь с id={}", user.getId());
            return user;
        } catch (ConstraintViolationException e) {
            log.error("Ошибка валидации");
            throw new ValidationException("Ошибка валидации");
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id {} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }
        try {
            users.put(user.getId(), user);
            log.info("Обновлён пользователь с id={}", user.getId());
            return user;
        } catch (ConstraintViolationException e) {
            log.error("Пользователь с id {} не найден", user.getId());
            throw new ValidationException("Ошибка валидации");
        }
    }
}

package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User deleteUser(Long userId);

    Set<User> getAll();
}

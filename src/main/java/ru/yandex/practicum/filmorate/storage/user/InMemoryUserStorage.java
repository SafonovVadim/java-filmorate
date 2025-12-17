package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.utils.UtilMethods.getNextId;


@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId(users));
        users.put(user.getId(), user);
        log.info("Создан пользователь с id={}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user == null) {
            log.error("Попытка обновить null пользователя");
            throw new NotFoundException("Пользователь не может быть null");
        }
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id {} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь с id={}", user.getId());
        return user;
    }

    @Override
    public User deleteUser(Long userId) {
        User user = getUserById(userId);
        users.remove(user.getId());
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    public User addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        updateUser(user);
        updateUser(friend);
        return user;
    }

    public User removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        updateUser(user);
        updateUser(friend);
        return user;
    }

    public Set<User> getFriends(Long userId) {
        User user = getUserById(userId);
        Set<Long> friends = user.getFriends();

        if (friends == null) {
            log.warn("У пользователя с id={} friends == null", userId);
            return Collections.emptySet();
        }

        return friends.stream()
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriend(Long userId, Long otherId) {
        User user = getUserById(userId);
        User other = getUserById(otherId);

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = other.getFriends();
        if (userFriends == null || otherFriends == null) {
            return Collections.emptySet();
        }
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }

    public User getUserById(Long userId) {
        if (userId == null || !users.containsKey(userId)) {
            log.error("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(userId);
    }
}

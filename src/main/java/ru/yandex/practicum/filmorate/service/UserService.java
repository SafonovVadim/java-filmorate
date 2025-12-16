package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Set;

@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User createUser(final User user) {
        return inMemoryUserStorage.createUser(user);
    }

    public User updateUser(final User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    public User deleteUser(final Long userId) {
        return inMemoryUserStorage.deleteUser(userId);
    }

    public Set<User> getAllUsers() {
        return inMemoryUserStorage.getAll();
    }

    public User addFriend(final Long userId, final Long friendId) {
        return inMemoryUserStorage.addFriend(userId, friendId);
    }

    public User removeFriend(final Long userId, final Long friendId) {
        return inMemoryUserStorage.removeFriend(userId, friendId);
    }

    public User getFavouriteFilms(final Long userId) {
        return null;
    }

    public Set<User> getCommonFriends(final Long userId, final Long otherId) {
        return inMemoryUserStorage.getCommonFriend(userId, otherId);
    }

    public Set<User> getAllFriends(final Long userId) {
        return inMemoryUserStorage.getFriends(userId);
    }
}

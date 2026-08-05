package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);


    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void checkId(Long id) {
        if (!userStorage.exists(id)) {
            String message = "Пользователь с id = " + id + " не найден";
            log.error(message);
            throw new IdNotFoundException(message);
        }
    }

    public void validate(User user) {

        if (user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            String message = "Логин не может быть пустым и содержать пробелы";
            throw new ValidationException(message);
        }
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        checkId(id);
        return userStorage.getUserById(id);
    }

    public User createUser(User user) {
        validate(user);
        User newUser = userStorage.createUser(user);
        log.info("Добавлен пользователь с id = " + newUser.getId());
        return newUser;
    }

    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        checkId(user.getId());
        User updateUser = userStorage.updateUser(user);
        log.info("Обновлён пользователь с id = " + updateUser.getId());
        return updateUser;
    }

    public User addFriend(Long id, Long friendId) {
        checkId(id);
        checkId(friendId);
        return userStorage.changeFriend(id, friendId, false);
    }

    public User deleteFriend(Long id, Long friendId) {
        checkId(id);
        checkId(friendId);
        return userStorage.changeFriend(id, friendId, true);
    }

    public Collection<User> getFriends(Long id) {
        checkId(id);
        User user = userStorage.getUserById(id);
        return userStorage.findAll().stream()
                .filter(u -> user.getFriends().contains(u.getId()))
                .toList();
    }

    public Collection<User> getCommonFriends(Long id, Long friendId) {
        checkId(id);
        checkId(friendId);
        HashSet<Long> list1 = userStorage.getUserById(id).getFriends();
        HashSet<Long> list2 = userStorage.getUserById(friendId).getFriends();
        List<Long> intersection = list1.stream()
                .filter(list2::contains)
                .toList();
        return userStorage.findAll().stream()
                .filter(u -> intersection.contains(u.getId()))
                .toList();
    }

}

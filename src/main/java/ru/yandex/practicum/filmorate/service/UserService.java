package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserNewRequest;
import ru.yandex.practicum.filmorate.dto.UserUpdateRequest;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final EventStorage eventStorage;

    private final UserStorage userStorage;

    public UserService(EventStorage eventStorage, UserStorage userStorage) {
        this.eventStorage = eventStorage;
        this.userStorage = userStorage;
    }

    public void validate(User user) {
        if (user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            String message = "Логин не может быть пустым и содержать пробелы";
            throw new ValidationException(message);
        }
    }

    public Collection<UserDto> findAll() {
        List<User> userList = userStorage.findAll().stream().toList();
        for (User user : userList) {
            user.setFriendlist((List<User>) userStorage.findFriends(user.getId()));
        }
        return userList.stream().map(UserMapper::mapToUserDto).toList();
    }

    public UserDto findById(Long id) {
        Optional<User> userOptional = userStorage.getUserById(id);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + id + " не найден");
        } else {
            return UserMapper.mapToUserDto(userOptional.get());
        }
        return UserMapper.mapToUserDto(userOptional.get());
    }

    public User createUser(UserNewRequest userNewRequest) {
        User user = UserMapper.mapToUser(userNewRequest);
        validate(user);
        User newUser = userStorage.createUser(user);
        log.info("Добавлен пользователь с id = {}", newUser.getId());
        return newUser;
    }

    public User updateUser(UserUpdateRequest userUpdateRequest) {
        User user = UserMapper.mapToUser(userUpdateRequest);
        validate(user);
        Optional<User> userOptional = userStorage.getUserById(user.getId());
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }

        log.info("Обновлён пользователь с id = {}", user.getId());
        return userStorage.updateUser(user).get();
    }

    public void deleteUser(Long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + userId + " не найден");
        }

        userStorage.deleteUser(userId);
        log.info("Удалён пользователь с id = {}", userId);
    }

    public User addFriend(Long id, Long friendId) {
        Optional<User> userOptional = userStorage.getUserById(id);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + id + " не найден");
        }
        userOptional = userStorage.getUserById(friendId);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        User user = userStorage.changeFriend(id, friendId,1)
                .orElseThrow(() -> new ValidationException("Не удалось добавить пользователя в друзья"));

        eventStorage.addEvent(id, EventType.FRIEND, Operation.ADD, friendId);

        return user;
    }

    public User deleteFriend(Long id, Long friendId) {
        Optional<User> userOptional = userStorage.getUserById(id);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + id + " не найден");
        }
        userOptional = userStorage.getUserById(friendId);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        User user = userStorage.changeFriend(id, friendId, 2)
                        .orElseThrow(() -> new ValidationException("Не удалось удалить пользователя из друзей"));

        eventStorage.addEvent(id, EventType.FRIEND, Operation.REMOVE, friendId);

        return user;
    }

    public User confirmFriend(Long id, Long friendId) {
        Optional<User> userOptional = userStorage.getUserById(id);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + id + " не найден");
        }
        userOptional = userStorage.getUserById(friendId);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        return userStorage.changeFriend(id, friendId, 3).get();
    }

    public Collection<User> getFriends(Long id) {
        Optional<User> userOptional = userStorage.getUserById(id);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + id + " не найден");
        }
        return userStorage.findFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long friendId) {
        Optional<User> userOptional = userStorage.getUserById(id);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + id + " не найден");
        }
        userOptional = userStorage.getUserById(friendId);
        if (userOptional.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        HashSet<Long> list1 = new HashSet<>(
                userStorage.findFriends(id).stream().map(User::getId).toList());
        HashSet<Long> list2 = new HashSet<>(
                userStorage.findFriends(friendId).stream().map(User::getId).toList());

        List<Long> intersection = list1.stream()
                .filter(list2::contains)
                .toList();
        return userStorage.findAll().stream()
                .filter(u -> intersection.contains(u.getId()))
                .toList();
    }
}

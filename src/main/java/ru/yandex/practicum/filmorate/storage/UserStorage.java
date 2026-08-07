package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Boolean exists(Long id);

    Collection<User> findAll();

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(User user);

    User deleteUser(User user);

    User changeFriend(Long id, Long friendId, Boolean remove);

}

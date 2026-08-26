package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Collection<User> findFriends(Long id);

    Optional<User> getUserById(Long id);

    User createUser(User user);

    Optional<User> updateUser(User user);

    User deleteUser(User user);

    Optional<User> changeFriend(Long id, Long friendId, Integer option);

    Collection<User> findLikesByFilm(Long id);

}

package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Boolean exists(Long id) {
        return users.containsKey(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        Long id = IdGenerator.getNextId(users);
        user.setId(id);
        users.put(id, user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        User oldUser = users.get(user.getId());
        oldUser.setEmail(user.getEmail());
        oldUser.setLogin(user.getLogin());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());
        if (oldUser.getName() == null || oldUser.getName().isBlank()) {
            oldUser.setName(user.getLogin());
        }
        return oldUser;
    }

    @Override
    public User deleteUser(User user) {
        users.remove(user.getId());
        return user;
    }

    @Override
    public User changeFriend(Long id, Long friendId, Boolean remove) {
        User user = users.get(id);
        User friend = users.get(friendId);
        if (remove) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(id);
        } else {
            user.getFriends().add(friendId);
            friend.getFriends().add(id);
        }
        return user;
    }
}

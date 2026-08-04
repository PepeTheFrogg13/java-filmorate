package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(UserController.class);


    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.validate();
        Long id = IdGenerator.getNextId(users);
        user.setId(id);
        users.put(id, user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Добавлен пользователь с id = " + user.getId());
        return user;
    }

    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) {
        user.validate();
        checkId(user.getId());
        User oldUser = users.get(user.getId());
        oldUser.setEmail(user.getEmail());
        oldUser.setLogin(user.getLogin());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());
        if (oldUser.getName() == null || oldUser.getName().isBlank()) {
            oldUser.setName(user.getLogin());
        }
        log.info("Обновлён пользователь с id = " + oldUser.getId());
        return oldUser;
    }


    private void checkId(Long id) {
        if (!users.containsKey(id)) {
            String message = "Пользователь с id = " + id + " не найден";
            log.error(message);
            throw new IdNotFoundException(message);
        }
    }
}

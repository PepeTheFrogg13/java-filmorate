package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

    private Film film;
    private User user;

    private final FilmStorage filmStorage = new InMemoryFilmStorage();

    private final UserStorage userStorage = new InMemoryUserStorage();

    private final UserService userService = new UserService(userStorage);

    private final FilmService filmService = new FilmService(filmStorage, userService);


    @BeforeEach
    void beforeEach() {
        film = new Film();
        user = new User();
    }

    @Test
    void testFilmOk() {
        film.setName("Test");
        film.setDescription("T".repeat(200));
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(123);
        assertDoesNotThrow(() -> filmService.validate(film));
    }

    @Test
    void testFilmName() {
        film.setName("");
        film.setDescription("Тестовое описание тестового фильма");
        film.setReleaseDate(LocalDate.of(2000, 12, 28));
        film.setDuration(123);
        ValidationException validationException = assertThrows(ValidationException.class, () -> filmService.validate(film));
        assertEquals("Название не может быть пустым", validationException.getMessage());
    }

    @Test
    void testFilmDescription() {
        film.setName("Test");
        film.setDescription("J".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 12, 12));
        film.setDuration(123);
        ValidationException validationException = assertThrows(ValidationException.class, () -> filmService.validate(film));
        assertEquals("Описание не может быть больше 200 символов", validationException.getMessage());
    }

    @Test
    void testFilmReleaseDate() {
        film.setName("Test");
        film.setDescription("J".repeat(200));
        film.setReleaseDate(LocalDate.of(1800, 12, 12));
        film.setDuration(123);
        ValidationException validationException = assertThrows(ValidationException.class, () -> filmService.validate(film));
        assertEquals("Дата фильма не может быть раньше 1895-12-28", validationException.getMessage());
    }

    @Test
    void testFilmDuration() {
        film.setName("Test");
        film.setDescription("J".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 12, 12));
        film.setDuration(-123);
        ValidationException validationException = assertThrows(ValidationException.class, () -> filmService.validate(film));
        assertEquals("Продолжительность фильма должна быть положительным числом", validationException.getMessage());
    }

    @Test
    void testUserOk() {
        user.setEmail("mikhail123@yandex.ru");
        user.setLogin("secret<3");
        user.setName("Boss");
        user.setBirthday(LocalDate.of(2000, 1, 29));
        assertDoesNotThrow(() -> userService.validate(user));
    }

    @Test
    void testUserEmailBlank() {
        user.setEmail("");
        user.setLogin("secret<3");
        user.setName("Boss");
        user.setBirthday(LocalDate.of(2000, 1, 29));
        ValidationException validationException = assertThrows(ValidationException.class, () -> userService.validate(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", validationException.getMessage());
    }

    @Test
    void testUserEmail() {
        user.setEmail("123123");
        user.setLogin("secret<3");
        user.setName("Boss");
        user.setBirthday(LocalDate.of(2000, 1, 29));
        ValidationException validationException = assertThrows(ValidationException.class, () -> userService.validate(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", validationException.getMessage());
    }

    @Test
    void testUserLoginSpace() {
        user.setEmail("mikhail123@yandex.ru");
        user.setLogin("secret< 3");
        user.setName("Boss");
        user.setBirthday(LocalDate.of(2000, 1, 29));
        ValidationException validationException = assertThrows(ValidationException.class, () -> userService.validate(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", validationException.getMessage());
    }

    @Test
    void testUserLoginEmpty() {
        user.setEmail("mikhail123@yandex.ru");
        user.setLogin("");
        user.setName("Boss");
        user.setBirthday(LocalDate.of(2000, 1, 29));
        ValidationException validationException = assertThrows(ValidationException.class, () -> userService.validate(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", validationException.getMessage());
    }

    @Test
    void testUserBirthDay() {
        user.setEmail("mikhail123@yandex.ru");
        user.setLogin("yes");
        user.setName("Boss");
        user.setBirthday(LocalDate.of(2040, 1, 29));
        ValidationException validationException = assertThrows(ValidationException.class, () -> userService.validate(user));
        assertEquals("Дата рождения не может быть в будущем", validationException.getMessage());
    }

}

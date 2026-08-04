package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
public class FilmService {

    private final Logger log = LoggerFactory.getLogger(FilmService.class);

    public void checkId(Long id) {
        if (!filmStorage.exists(id)) {
            String message = "Фильм с id = " + id + " не найден";
            log.error(message);
            throw new IdNotFoundException(message);
        }
    }

    @Autowired
    private FilmStorage filmStorage;
    //Внедряем зависимость UserService, чтобы опрашивать его насчет существования пользователя
    @Autowired
    private UserService userService;


    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        checkId(id);
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        film.validate();
        Film newFilm = filmStorage.createFilm(film);
        log.info("Создан фильм с id = " + newFilm.getId());
        return newFilm;

    }

    public Film updateFilm(Film film) {
        film.validate();
        checkId(film.getId());
        Film updateFilm = filmStorage.updateFilm(film);
        log.info("Изменён фильм с id = " + updateFilm.getId());
        return updateFilm;
    }

    public Film deleteFilm(Film film) {
        checkId(film.getId());
        Film deleteFilm = filmStorage.deleteFilm(film);
        log.info("Удалён фильм с id = " + deleteFilm.getId());
        return deleteFilm;
    }

    public Film addLike(Long id, Long userId) {
        checkId(id);
        userService.checkId(userId);
        return filmStorage.changeLikes(id, userId, false);
    }

    public Film deleteLike(Long id, Long userId) {
        checkId(id);
        userService.checkId(userId);
        return filmStorage.changeLikes(id, userId, true);
    }

    public Collection<Film> findTop(Integer top) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(top).toList();
    }

}

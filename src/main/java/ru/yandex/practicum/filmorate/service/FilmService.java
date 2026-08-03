package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
public class FilmService {

    private final Logger log = LoggerFactory.getLogger(FilmService.class);

    private void checkId(Long id) {
        if (!filmStorage.exists(id)) {
            String message = "Фильм с id = " + id + " не найден";
            log.error(message);
            throw new IdNotFoundException(message);
        }
    }

    private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        film.validate();
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        film.validate();
        checkId(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film deleteFilm(Film film) {
        checkId(film.getId());
        return filmStorage.deleteFilm(film);
    }

    public Film addLike(Long id, Long userId) {
        checkId(id);
        return filmStorage.changeLikes(id, userId, false);
    }

    public Film deleteLike(Long id, Long userId) {
        checkId(id);
        return filmStorage.changeLikes(id, userId, true);
    }

    public Collection<Film> findTop(Integer top) {
        return filmStorage.findAll().stream()
                .sorted((f1,f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(top).toList();
    }

}

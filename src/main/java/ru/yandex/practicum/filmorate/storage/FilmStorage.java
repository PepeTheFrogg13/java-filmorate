package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Boolean exists(Long id);

    Collection<Film> findAll();

    Film getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(Film film);

    Film changeLikes(Long id, Long userId, Boolean remove);

}

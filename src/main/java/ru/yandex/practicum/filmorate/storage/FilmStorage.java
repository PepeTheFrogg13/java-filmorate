package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;


public interface FilmStorage {

    Collection<Film> findAll();

    Optional<Film> getFilmById(Long id);

    Film createFilm(Film film);

    Optional<Film> updateFilm(Film film);

    void deleteFilm(Long id);

    Optional<Film> changeLikes(Long id, Long userId, Boolean remove);

    Collection<Film> findTopLikes(Integer top);

    Collection<Film> findTopLikes(Integer top, Long genreId, Integer year);


}

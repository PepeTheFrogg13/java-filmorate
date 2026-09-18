package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {

    List<Director> findAll();

    Optional<Director> findById(Long id);

    Director save(Director director);

    Optional<Director> update(Director director);

    void delete(Long id);

    List<Director> findDirectorsByFilmId(Long filmId);

    void addDirectorsToFilm(Long filmId, Set<Director> directors);

    void removeDirectorsFromFilm(Long filmId);

}

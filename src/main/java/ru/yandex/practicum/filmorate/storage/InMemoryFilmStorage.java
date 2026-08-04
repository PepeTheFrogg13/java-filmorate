package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Boolean exists(Long id) {
        return films.containsKey(id);
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    @Override
    public Film createFilm(Film film) {
        Long id = IdGenerator.getNextId(films);
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        return oldFilm;
    }

    @Override
    public Film deleteFilm(Film film) {
        films.remove(film.getId());
        return film;
    }

    @Override
    public Film changeLikes(Long id, Long userId, Boolean remove) {
        Film film = films.get(id);
        if (remove){
            film.getLikes().remove(userId);
        } else {
            film.getLikes().add(userId);
        }
        return film;
    }

}

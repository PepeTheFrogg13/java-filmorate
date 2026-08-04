package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    private final Logger log = LoggerFactory.getLogger(FilmController.class);


    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.validate();
        Long id = IdGenerator.getNextId(films);
        film.setId(id);
        films.put(id, film);
        log.info("Создан фильм с id = " + film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        film.validate();
        checkId(film.getId());
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        log.info("Обновлён фильм с id = " + oldFilm.getId());
        return oldFilm;
    }

    private void checkId(Long id) {
        if (!films.containsKey(id)) {
            String message = "Фильм с id = " + id + " не найден";
            log.error(message);
            throw new IdNotFoundException(message);
        }
    }
}

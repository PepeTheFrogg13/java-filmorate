package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmNewRequest;
import ru.yandex.practicum.filmorate.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    public FilmDto findById(@PathVariable Long filmId) {
        return filmService.findById(filmId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.findTop(count);
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody FilmNewRequest film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody @Valid FilmUpdateRequest film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film dislikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFilmsByDirector(
            @PathVariable Long directorId,
            @RequestParam(defaultValue = "year") String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }
}

package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmNewRequest;
import ru.yandex.practicum.filmorate.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.dto.GenreInsert;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {

    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    private final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;
    private final FilmGenreStorage filmGenreStorage;


    public FilmService(FilmStorage filmStorage,
                       RatingStorage ratingStorage,
                       GenreStorage genreStorage,
                       UserStorage userStorage, FilmGenreStorage filmGenreStorage) {
        this.filmStorage = filmStorage;
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
        this.userStorage = userStorage;
        this.filmGenreStorage = filmGenreStorage;
    }

    public void validate(Film film) {

        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            String message = "Дата фильма не может быть раньше " + MIN_DATE;
            throw new ValidationException(message);
        }
    }

    public Collection<FilmDto> findAll() {
        Map<Long, List> filmGenres = filmGenreStorage.getFilmGenres();
        List<Film> filmList = filmStorage.findAll().stream().toList();
        for (Film film : filmList) {
            film.setGenreList(filmGenres.get(film.getId()));
        }
        for (Film film : filmList) {
            film.setLikeList(userStorage.findLikesByFilm(film.getId()).stream().toList());
        }
        return filmList.stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public FilmDto findById(Long id) {
        Optional<Film> filmOptional = filmStorage.getFilmById(id);
        if (filmOptional.isEmpty()) {
            throw new IdNotFoundException("Фильм с id = " + id + " не найден");
        } else {
            Film film = filmOptional.get();
            film.setGenreList(genreStorage.findByFilm(film.getId()).stream().toList());
            film.setLikeList(userStorage.findLikesByFilm(film.getId()).stream().toList());
            return FilmMapper.mapToFilmDto(film);
        }
    }

    public FilmDto createFilm(FilmNewRequest filmNewRequest) {
        Film newFilm = FilmMapper.mapToFilm(filmNewRequest);
        validate(newFilm);
        Optional<Rating> ratingOptional = ratingStorage.findById(filmNewRequest.getMpa().getId());
        if (ratingOptional.isEmpty()) {
            throw new IdNotFoundException("Рейтинг с id = " + filmNewRequest.getMpa().getId() + " не найден");
        }
        newFilm.setRating(ratingOptional.get());
        for (Long id : filmNewRequest.getGenres().stream().map(GenreInsert::getId).toList()) {
            Optional<Genre> genreOptional = genreStorage.findById(id);
            if (genreOptional.isEmpty()) {
                throw new IdNotFoundException("Жанр с id = " + id + " не найден");
            } else {
                newFilm.getGenreList().add(genreOptional.get());
            }
        }
        filmStorage.createFilm(newFilm).getId();
        log.info("Создан фильм с id = " + newFilm.getId());
        return FilmMapper.mapToFilmDto(newFilm);
    }

    public FilmDto updateFilm(FilmUpdateRequest filmUpdateRequest) {
        Film film = FilmMapper.mapToFilm(filmUpdateRequest);
        Optional<Film> filmOptional = filmStorage.updateFilm(film);
        if (filmOptional.isEmpty()) {
            throw new IdNotFoundException("Фильм с id = " + film.getId() + " не найден");
        }

        Optional<Rating> ratingOptional = ratingStorage.findById(filmUpdateRequest.getMpa().getId());


        if (ratingOptional.isEmpty()) {
            throw new IdNotFoundException("Рейтинг с id = " + filmUpdateRequest.getMpa().getId() + " не найден");
        }
        film.setRating(ratingOptional.get());


        for (Long id : filmUpdateRequest.getGenres().stream().map(GenreInsert::getId).toList()) {
            Optional<Genre> genreOptional = genreStorage.findById(id);
            if (genreOptional.isEmpty()) {
                throw new IdNotFoundException("Жанр с id = " + id + " не найден");
            } else {
                film.getGenreList().add(genreOptional.get());
            }
        }
        return FilmMapper.mapToFilmDto(film);
    }

    public Film deleteFilm(Film film) {
        return filmStorage.deleteFilm(film);
    }

    public Film addLike(Long id, Long userId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + id + " не найден");
        }
        Optional<Film> optionalFilm = filmStorage.changeLikes(id, userId, false);
        if (optionalFilm.isEmpty()) {
            throw new IdNotFoundException("Фильм с id = " + id + " не найден");
        } else {
            return optionalFilm.get();
        }
    }

    public Film deleteLike(Long id, Long userId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + id + " не найден");
        }
        Optional<Film> optionalFilm = filmStorage.changeLikes(id, userId, true);
        if (optionalFilm.isEmpty()) {
            throw new IdNotFoundException("Фильм с id = " + id + " не найден");
        } else {
            return optionalFilm.get();
        }
    }

    public Collection<FilmDto> findTop(Integer top) {
        return findTop(top, null, null);
    }

    public Collection<FilmDto> findTop(Integer top, Long genreId, Integer year) {
        Map<Long, List> filmGenres = filmGenreStorage.getFilmGenres();
        List<Film> filmList = filmStorage.findTopLikes(top, genreId, year).stream().toList();
        for (Film film : filmList) {
            film.setGenreList(filmGenres.getOrDefault(film.getId(), new ArrayList<Genre>()));
            film.setLikeList(userStorage.findLikesByFilm(film.getId()).stream().toList());
        }
        return filmList.stream().map(FilmMapper::mapToFilmDto).toList();
    }

}

package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmNewRequest;
import ru.yandex.practicum.filmorate.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.dto.GenreInsert;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    private final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final DirectorDbStorage directorStorage;
    private final DirectorService directorService;
    private final EventStorage eventStorage;
    private final FilmDirectorStorage filmDirectorStorage;


    public FilmService(FilmStorage filmStorage,
                       RatingStorage ratingStorage,
                       GenreStorage genreStorage,
                       UserStorage userStorage,
                       FilmGenreStorage filmGenreStorage, DirectorDbStorage directorStorage,
                       DirectorService directorService,
                       EventStorage eventStorage, FilmDirectorStorage filmDirectorStorage) {
        this.filmStorage = filmStorage;
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
        this.userStorage = userStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.directorStorage = directorStorage;
        this.directorService = directorService;
        this.eventStorage = eventStorage;
        this.filmDirectorStorage = filmDirectorStorage;
    }

    public void validate(Film film) {

        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            String message = "Дата фильма не может быть раньше " + MIN_DATE;
            throw new ValidationException(message);
        }
    }

    public Collection<FilmDto> findAll() {
        Map<Long, List> filmGenres = filmGenreStorage.getFilmGenres();
        Map<Long, List> filmDirectors = filmDirectorStorage.getFilmDirectors();
        List<Film> filmList = filmStorage.findAll().stream().toList();
        for (Film film : filmList) {
            if (filmGenres.containsKey(film.getId())) {
                film.setGenreList(filmGenres.get(film.getId()));
            }
        }
        for (Film film : filmList) {
            film.setLikeList(userStorage.findLikesByFilm(film.getId()).stream().toList());
        }
        for (Film film : filmList) {
            if (filmDirectors.containsKey(film.getId())) {
                film.setDirectors(new HashSet<>(filmDirectors.get(film.getId())));
            }
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
            film.setDirectors(directorStorage.findDirectorsByFilmId(film.getId()).stream().collect(Collectors.toSet()));
            return FilmMapper.mapToFilmDto(film);
        }
    }

    @Transactional
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
        if (filmNewRequest.getDirectors() != null && !filmNewRequest.getDirectors().isEmpty()) {
            Set<Director> directors = new HashSet<>();
            for (var directorDto : filmNewRequest.getDirectors()) {
                Long directorId = directorDto.getId();
                Director director = directorService.getDirectorById(directorId);
                directors.add(director);
            }
            newFilm.setDirectors(directors);
        }
        filmStorage.createFilm(newFilm).getId();
        log.info("Создан фильм с id = " + newFilm.getId());
        return FilmMapper.mapToFilmDto(newFilm);
    }

    public FilmDto updateFilm(FilmUpdateRequest filmUpdateRequest) {
        Film film = FilmMapper.mapToFilm(filmUpdateRequest);
        Optional<Film> filmOptional = filmStorage.getFilmById(film.getId());
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

        for (Long directorId : filmUpdateRequest.getDirectors().stream().map(Director::getId).toList()) {

            Optional<Director> directorOptional = directorStorage.findById(directorId);
            if (directorOptional.isEmpty()) {
                throw new IdNotFoundException("Режиссёр с id = " + directorId + " не найден");
            } else {
                film.getDirectors().add(directorOptional.get());
            }
        }

        filmStorage.updateFilm(film);

        return FilmMapper.mapToFilmDto(filmStorage.getFilmById(film.getId()).get());
    }

    public Film deleteFilm(Long id) {
        Optional<Film> filmOptional = filmStorage.getFilmById(id);
        if (filmOptional.isEmpty()) {
            throw new IdNotFoundException("Фильм с id = " + id + " не найден");
        }
        return filmStorage.deleteFilm(filmOptional.get());
    }

    public Film addLike(Long id, Long userId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + userId + " не найден");
        }

        Optional<Film> optionalFilm = filmStorage.changeLikes(id, userId, false);
        if (optionalFilm.isEmpty()) {
            throw new IdNotFoundException("Фильм с id = " + id + " не найден");
        }

        eventStorage.addEvent(userId, EventType.LIKE, Operation.ADD, id);

        return optionalFilm.get();
    }

    public Film deleteLike(Long id, Long userId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        Optional<Film> optionalFilm = filmStorage.changeLikes(id, userId, true);
        if (optionalFilm.isEmpty()) {
            throw new IdNotFoundException("Фильм с id = " + id + " не найден");
        }

        eventStorage.addEvent(userId, EventType.LIKE, Operation.REMOVE, id);

        return optionalFilm.get();
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


    public Collection<FilmDto> getRecommendations(Long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + userId + " не найден");
        }

        List<Film> filmList = filmStorage.findRecommendations(userId).stream().toList();
        Map<Long, List> filmGenres = filmGenreStorage.getFilmGenres();

        for (Film film : filmList) {
            film.setGenreList(filmGenres.getOrDefault(film.getId(), new ArrayList<Genre>()));
            film.setLikeList(userStorage.findLikesByFilm(film.getId()).stream().toList());
        }

        return filmList.stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public List<FilmDto> getFilmsByDirector(Long directorId, String sortBy) {
        directorService.getDirectorById(directorId);

        if (!"year".equalsIgnoreCase(sortBy) && !"likes".equalsIgnoreCase(sortBy)) {
            throw new ValidationException("Параметр sortBy должен быть 'year' или 'likes'");
        }
        List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);
        Map<Long, List> filmGenres = filmGenreStorage.getFilmGenres();
        for (Film film : films) {
            film.setGenreList(filmGenres.getOrDefault(film.getId(), new ArrayList<>()));
            film.setLikeList(userStorage.findLikesByFilm(film.getId()).stream().toList());
        }

        return films.stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public List<FilmDto> searchFilms(String query, String by) {
        if (query == null || query.isBlank()) {
            throw new ValidationException("Query не может быть пустым");
        }
        String[] criteria = by.split(",");
        for (String c : criteria) {
            String trimmed = c.trim().toLowerCase();
            if (!"title".equals(trimmed) && !"director".equals(trimmed)) {
                throw new ValidationException("Недопустимый критерий поиска: " + c);
            }
        }

        List<Film> films = filmStorage.search(query, by);

        Map<Long, List> filmGenres = filmGenreStorage.getFilmGenres();
        for (Film film : films) {
            film.setGenreList(filmGenres.getOrDefault(film.getId(), new ArrayList<>()));
            film.setLikeList(userStorage.findLikesByFilm(film.getId()).stream().toList());
        }


        return films.stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public Collection<FilmDto> findCommonFilms(Long userId, Long friendId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userStorage.getUserById(friendId).isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        return mapFilmsToDto(filmStorage.findCommonFilms(userId, friendId));
    }

    @SuppressWarnings("unchecked")
    private Collection<FilmDto> mapFilmsToDto(Collection<Film> films) {
        Map<Long, List> filmGenres = filmGenreStorage.getFilmGenres();
        List<Film> filmList = films.stream().toList();

        for (Film film : filmList) {
            film.setGenreList(filmGenres.get(film.getId()));
        }
        for (Film film : filmList) {
            List<Genre> genres = filmGenres.containsKey(film.getId())
                    ? (List<Genre>) filmGenres.get(film.getId())
                    : new ArrayList<>();
            film.setGenreList(genres);
            film.setLikeList(userStorage.findLikesByFilm(film.getId()).stream().toList());
        }
        return filmList.stream().map(FilmMapper::mapToFilmDto).toList();
    }


}

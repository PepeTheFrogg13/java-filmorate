package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmNewRequest;
import ru.yandex.practicum.filmorate.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;


@Component
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getLong("RatingId"));
        rating.setName(rs.getString("RatingName"));
        Film film = new Film();
        film.setId(rs.getLong("FilmId"));
        film.setName(rs.getString("Name"));
        film.setDescription(rs.getString("Description"));
        film.setReleaseDate(rs.getDate("ReleaseDate").toLocalDate());
        film.setDuration(rs.getInt("Duration"));
        film.setRating(rating);
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDuration(film.getDuration());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setMpa(film.getRating());
        filmDto.setGenres(film.getGenreList());
        filmDto.setLikeList(film.getLikeList());
        filmDto.setDirectors(film.getDirectors());
        return filmDto;
    }

    public static Film mapToFilm(FilmNewRequest filmNewRequest) {
        Film film = new Film();
        film.setName(filmNewRequest.getName());
        film.setDescription(filmNewRequest.getDescription());
        film.setDuration(filmNewRequest.getDuration());
        film.setReleaseDate(filmNewRequest.getReleaseDate());

        if (filmNewRequest.getDirectors() != null && !filmNewRequest.getDirectors().isEmpty()) {
            film.setDirectors(new HashSet<>(filmNewRequest.getDirectors()));
        }
        return film;
    }

    public static Film mapToFilm(FilmUpdateRequest filmUpdateRequest) {
        Film film = new Film();
        film.setId(filmUpdateRequest.getId());
        film.setName(filmUpdateRequest.getName());
        film.setDescription(filmUpdateRequest.getDescription());
        film.setDuration(filmUpdateRequest.getDuration());
        film.setReleaseDate(filmUpdateRequest.getReleaseDate());

        if (filmUpdateRequest.getDirectors() != null && !filmUpdateRequest.getDirectors().isEmpty()) {
            film.setDirectors(new HashSet<>(filmUpdateRequest.getDirectors()));
        }
        return film;
    }
}

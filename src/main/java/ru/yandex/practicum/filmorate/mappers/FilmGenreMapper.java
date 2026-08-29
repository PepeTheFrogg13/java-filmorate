package ru.yandex.practicum.filmorate.mappers;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmGenreMapper implements RowMapper<FilmGenre> {

    @Override
    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("GenreId"));
        genre.setName(rs.getString("Name"));
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setFilmId(rs.getLong("FilmId"));
        filmGenre.setFilmGenre(genre);
        return filmGenre;
    }
}

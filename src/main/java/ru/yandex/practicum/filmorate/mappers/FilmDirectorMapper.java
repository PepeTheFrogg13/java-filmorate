package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmDirectorMapper implements RowMapper<FilmDirector> {
    @Override
    public FilmDirector mapRow(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("DirectorId"));
        director.setName(rs.getString("Name"));
        FilmDirector filmDirector = new FilmDirector();
        Long id = rs.getLong("FilmId");
        filmDirector.setFilmId(id);
        filmDirector.setDirector(director);
        return filmDirector;
    }
}

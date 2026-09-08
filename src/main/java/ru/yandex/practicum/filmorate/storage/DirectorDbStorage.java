package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Set;

@Repository
public class DirectorDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Director> DIRECTOR_ROW_MAPPER = (rs, rowNum) -> {
        Director director = new Director();
        director.setId(rs.getLong("DirectorId"));
        director.setName(rs.getString("Name"));
        return director;
    };

    public List<Director> findAll() {
        String sql = "SELECT \"DirectorId\", \"Name\" FROM \"directors\"";
        return jdbcTemplate.query(sql, DIRECTOR_ROW_MAPPER);
    }

    public Director findById(Long id) {
        String sql = "SELECT \"DirectorId\", \"Name\" FROM \"directors\" WHERE \"DirectorId\" = ?";
        List<Director> result = jdbcTemplate.query(sql, DIRECTOR_ROW_MAPPER, id);
        if (result.isEmpty()) {
            throw new ValidationException("Режиссёр с id " + id + " не найден");
        }
        return result.get(0);
    }

    public Director save(Director director) {
        String sql = "INSERT INTO \"directors\" (\"Name\") VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"DirectorId"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    public Director update(Director director) {
        String sql = "UPDATE \"directors\" SET \"Name\" = ? WHERE \"DirectorId\" = ?";
        int rows = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (rows == 0) {
            throw new ValidationException("Режиссёр с id " + director.getId() + " не найден");
        }
        return director;
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM \"film_director\" WHERE \"DirectorId\" = ?", id);
        int rows = jdbcTemplate.update("DELETE FROM \"directors\" WHERE \"DirectorId\" = ?", id);
        if (rows == 0) {
            throw new ValidationException("Режиссёр с id " + id + " не найден");
        }
    }

    public List<Director> findDirectorsByFilmId(Long filmId) {
        String sql = "SELECT d.\"DirectorId\", d.\"Name\" " +
                "FROM \"directors\" d " +
                "JOIN \"film_director\" fd ON d.\"DirectorId\" = fd.\"DirectorId\" " +
                "WHERE fd.\"FilmId\" = ?";
        return jdbcTemplate.query(sql, DIRECTOR_ROW_MAPPER, filmId);
    }

    public void addDirectorsToFilm(Long filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO \"film_director\" (\"FilmId\", \"DirectorId\") VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, directors, directors.size(),
                (ps, director) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, director.getId());
                });
    }

    public void removeDirectorsFromFilm(Long filmId) {
        jdbcTemplate.update("DELETE FROM \"film_director\" WHERE \"FilmId\" = ?", filmId);
    }
}
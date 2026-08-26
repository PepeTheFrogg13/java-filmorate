package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    private static final String GENRE_ALL = "SELECT * FROM \"Genre\"";
    private static final String GENRE_BY_ID = "SELECT * FROM \"Genre\" WHERE \"Genre\".\"GenreId\" = ?;";
    private static final String FIND_FILM_GENRES = "SELECT \"Genre\".* FROM \"FilmGenre\" INNER JOIN \"Genre\" ON \"Genre\".\"GenreId\"  = \"FilmGenre\".\"GenreId\" WHERE \"FilmGenre\".\"FilmId\" = ?;";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany(GENRE_ALL);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return findOne(GENRE_BY_ID, id);
    }

    @Override
    public Collection<Genre> findByFilm(Long id) {
        return findMany(FIND_FILM_GENRES, id);
    }
}

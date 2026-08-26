package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;


import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_FILMS = "SELECT \"Film\".*, \"Rating\".\"Name\" AS \"RatingName\"  FROM \"Film\" LEFT JOIN \"Rating\" ON \"Rating\".\"RatingId\" = \"Film\".\"RatingId\";";
    private static final String FIND_FILM_BY_ID = "SELECT \"Film\".*, \"Rating\".\"Name\" AS \"RatingName\"  FROM \"Film\" LEFT JOIN \"Rating\" ON \"Rating\".\"RatingId\" = \"Film\".\"RatingId\" WHERE \"Film\".\"FilmId\" = ?;";

    private static final String INSERT_FILM = "INSERT INTO \"Film\" (\"Name\",\"Description\",\"ReleaseDate\",\"Duration\", \"RatingId\") VALUES (?,?,?,?,?);";
    private static final String INSERT_FILM_GENRE = "MERGE INTO \"FilmGenre\" (\"FilmId\",\"GenreId\") KEY(\"FilmId\",\"GenreId\") VALUES (?,?);";

    private static final String UPDATE_FILM = "UPDATE \"Film\" SET \"Name\" = ?, \"Description\" = ?, \"ReleaseDate\" = ?, \"Duration\" = ? WHERE \"FilmId\" = ?;";

    private static final String DELETE_FILM = "DELETE FROM \"Film\" WHERE \"Film\".\"FilmId\" = ?;";
    private static final String DELETE_FILM_GENRE = "DELETE FROM \"FilmGenre\" WHERE \"FilmId\" = ? AND \"FilmGenreId\" = ?;";

    private static final String INSERT_LIKE = "INSERT INTO \"FilmLikes\" (\"FilmId\",\"UserID\") VALUES (?,?);";
    private static final String DELETE_LIKE = "DELETE FROM \"FilmLikes\" WHERE \"FilmId\" = ? AND \"UserID\" = ?;";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        return findMany(FIND_ALL_FILMS);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return findOne(FIND_FILM_BY_ID, id);
    }

    @Override
    public Film createFilm(Film film) {
        Long ratingId = film.getRating().getId();
        Long id = insert(INSERT_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), ratingId);
        for (Genre genre : film.getGenreList()) {
            insert(INSERT_FILM_GENRE, id, genre.getId());
        }
        film.setId(id);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        Film oldFilm = getFilmById(film.getId()).get();
        update(UPDATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        for (Genre genre : oldFilm.getGenreList()) {
            if (!film.getGenreList().contains(genre)) {
                update(DELETE_FILM_GENRE, film.getId(), genre.getId());
            }
        }
        for (Genre genre : film.getGenreList()) {
            if (!oldFilm.getGenreList().contains(genre)) {
                insert(INSERT_FILM_GENRE, film.getId(), genre.getId());
            }
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film deleteFilm(Film film) {
        delete(DELETE_FILM, film.getId());
        return film;
    }

    @Override
    public Optional<Film> changeLikes(Long id, Long userId, Boolean remove) {
        if (!remove) {
            insert(INSERT_LIKE, id, userId);
        } else {
            update(DELETE_LIKE, id, userId);
        }
        return getFilmById(id);
    }
}

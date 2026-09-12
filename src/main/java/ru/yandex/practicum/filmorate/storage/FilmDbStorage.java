package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;


import java.util.ArrayList;
import java.util.Collection;
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
    private static final String DELETE_FILM_GENRES = "DELETE FROM \"FilmGenre\" WHERE \"FilmId\" = ?;";
    private static final String DELETE_FILM_GENRE = "DELETE FROM \"FilmGenre\" WHERE \"FilmId\" = ? AND \"GenreId\" = ?;";
    private static final String DELETE_FILM_LIKES = "DELETE FROM \"FilmLikes\" WHERE \"FilmId\" = ?;";

    private static final String INSERT_LIKE = "MERGE INTO \"FilmLikes\" (\"FilmId\",\"UserID\") KEY (\"FilmId\",\"UserID\") VALUES (?,?);";
    private static final String DELETE_LIKE = "DELETE FROM \"FilmLikes\" WHERE \"FilmId\" = ? AND \"UserID\" = ?;";

    private static final String FIND_TOP_LIKES = "SELECT \"Film\".*,\n" +
            "\t   \"Rating\".\"Name\" AS \"RatingName\"\n" +
            "  FROM \"Film\" \n" +
            "  \t   LEFT JOIN \"Rating\" ON \"Rating\".\"RatingId\" = \"Film\".\"RatingId\" \n" +
            "  \t   LEFT JOIN (SELECT \"FilmLikes\".\"FilmId\",\n" +
            "\t\t  \t\t\t      COUNT(\"FilmLikes\".\"FilmLikesId\") AS FilmLikesCount\n" +
            "\t                FROM \"FilmLikes\"  \t\n" +
            "                GROUP BY \"FilmLikes\".\"FilmId\") TAB_FILM_LIKES ON TAB_FILM_LIKES.\"FilmId\" = \"Film\".\"FilmId\" \n" +
            " ORDER BY TAB_FILM_LIKES.FILMLIKESCOUNT  DESC    \n" +
            "  LIMIT ?;";

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
        Optional<Film> filmOptional = getFilmById(film.getId());
        if (filmOptional.isEmpty()) {
            throw new IdNotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        Film oldFilm = filmOptional.get();
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
    @Transactional
    public void deleteFilm(Long id) {
        jdbc.update(DELETE_FILM_LIKES, id);
        jdbc.update(DELETE_FILM_GENRES, id);
        jdbc.update(DELETE_FILM, id);
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

    @Override
    public Collection<Film> findTopLikes(Integer top) {
        return findMany(FIND_TOP_LIKES, top);
    }

    @Override
    public Collection<Film> findTopLikes(Integer top, Long genreId, Integer year) {
        StringBuilder query = new StringBuilder(
                "SELECT f.*, r.\"Name\" AS \"RatingName\" " +
                        "FROM \"Film\" f " +
                        "LEFT JOIN \"Rating\" r ON r.\"RatingId\" = f.\"RatingId\" " +
                        "LEFT JOIN (" +
                        "    SELECT \"FilmId\", COUNT(*) AS likes_count " +
                        "    FROM \"FilmLikes\" " +
                        "    GROUP BY \"FilmId\"" +
                        ") lc ON lc.\"FilmId\" = f.\"FilmId\" " +
                        "WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();

        if (genreId != null) {
            query.append("AND EXISTS (" +
                    "SELECT 1 FROM \"FilmGenre\" fg " +
                    "WHERE fg.\"FilmId\" = f.\"FilmId\" AND fg.\"GenreId\" = ?" +
                    ") ");
            params.add(genreId);
        }

        if (year != null) {
            query.append("AND EXTRACT(YEAR FROM f.\"ReleaseDate\") = ? ");
            params.add(year);
        }

        query.append("ORDER BY COALESCE(lc.likes_count, 0) DESC, f.\"FilmId\" ASC LIMIT ?;");
        params.add(top);

        return findMany(query.toString(), params.toArray());
    }


}

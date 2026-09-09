package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_FILMS = "SELECT \"Film\".*, \"Rating\".\"Name\" AS \"RatingName\"  FROM \"Film\" LEFT JOIN \"Rating\" ON \"Rating\".\"RatingId\" = \"Film\".\"RatingId\";";
    private static final String FIND_FILM_BY_ID = "SELECT \"Film\".*, \"Rating\".\"Name\" AS \"RatingName\"  FROM \"Film\" LEFT JOIN \"Rating\" ON \"Rating\".\"RatingId\" = \"Film\".\"RatingId\" WHERE \"Film\".\"FilmId\" = ?;";

    private static final String INSERT_FILM = "INSERT INTO \"Film\" (\"Name\",\"Description\",\"ReleaseDate\",\"Duration\", \"RatingId\") VALUES (?,?,?,?,?);";
    private static final String INSERT_FILM_GENRE = "MERGE INTO \"FilmGenre\" (\"FilmId\",\"GenreId\") KEY(\"FilmId\",\"GenreId\") VALUES (?,?);";

    private static final String UPDATE_FILM = "UPDATE \"Film\" SET \"Name\" = ?, \"Description\" = ?, \"ReleaseDate\" = ?, \"Duration\" = ? WHERE \"FilmId\" = ?;";

    private static final String DELETE_FILM = "DELETE FROM \"Film\" WHERE \"Film\".\"FilmId\" = ?;";
    private static final String DELETE_FILM_GENRE = "DELETE FROM \"FilmGenre\" WHERE \"FilmId\" = ? AND \"GenreId\" = ?;";

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

    // Получить фильмы конкретного режиссёра с сортировкой
    private static final String FIND_FILMS_BY_DIRECTOR =
            "SELECT f.\"FilmId\", f.\"Name\", f.\"Description\", f.\"ReleaseDate\", f.\"Duration\", f.\"RatingId\", r.\"Name\" AS \"RatingName\" " +
                    "FROM \"Film\" f " +
                    "JOIN \"film_director\" fd ON f.\"FilmId\" = fd.\"FilmId\" " +
                    "LEFT JOIN \"Rating\" r ON f.\"RatingId\" = r.\"RatingId\" " +
                    "WHERE fd.\"DirectorId\" = ?";

    // Сортировка по году выпуска
    private static final String ORDER_BY_YEAR = " ORDER BY f.\"ReleaseDate\"";

    // Сортировка по количеству лайков
    private static final String ORDER_BY_LIKES =
            " ORDER BY (SELECT COUNT(*) FROM \"FilmLikes\" WHERE \"FilmId\" = f.\"FilmId\") DESC";

    // Базовый запрос для поиска по режиссеру
    private static final String SEARCH_BASE =
            "SELECT DISTINCT f.\"FilmId\", f.\"Name\", f.\"Description\", f.\"ReleaseDate\", f.\"Duration\", f.\"RatingId\", r.\"Name\" AS \"RatingName\" " +
                    "FROM \"Film\" f " +
                    "LEFT JOIN \"film_director\" fd ON f.\"FilmId\" = fd.\"FilmId\" " +
                    "LEFT JOIN \"directors\" d ON fd.\"DirectorId\" = d.\"DirectorId\" " +
                    "LEFT JOIN \"Rating\" r ON f.\"RatingId\" = r.\"RatingId\" " +
                    "WHERE 1=1";

    private static final String ORDER_BY_POPULARITY =
            " ORDER BY (SELECT COUNT(*) FROM \"FilmLikes\" WHERE \"FilmId\" = f.\"FilmId\") DESC";

    private final DirectorDbStorage directorDbStorage;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, DirectorDbStorage directorDbStorage) {
        super(jdbc, mapper);
        this.directorDbStorage = directorDbStorage;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_FILMS);
        for (Film film : films) {
            List<Director> directors = directorDbStorage.findDirectorsByFilmId(film.getId());
            film.setDirectors(new HashSet<>(directors));
        }
        return films;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        Optional<Film> filmOpt = findOne(FIND_FILM_BY_ID, id);
        if (filmOpt.isPresent()) {
            Film film = filmOpt.get();
            List<Director> directors = directorDbStorage.findDirectorsByFilmId(film.getId());
            film.setDirectors(new HashSet<>(directors));
        }
        return filmOpt;
    }

    @Override
    public Film createFilm(Film film) {
        Long ratingId = film.getRating() != null ? film.getRating().getId() : null;
        Long id = insert(INSERT_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), ratingId);
        film.setId(id);

        for (Genre genre : film.getGenreList()) {
            insert(INSERT_FILM_GENRE, id, genre.getId());
        }
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            directorDbStorage.addDirectorsToFilm(id, film.getDirectors());
        }
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

        // Обновляем жанры
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
        directorDbStorage.removeDirectorsFromFilm(film.getId());
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            directorDbStorage.addDirectorsToFilm(film.getId(), film.getDirectors());
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

    @Override
    public Collection<Film> findTopLikes(Integer top) {
        List<Film> films = (List<Film>) findMany(FIND_TOP_LIKES, top);
        for (Film film : films) {
            List<Director> directors = directorDbStorage.findDirectorsByFilmId(film.getId());
            film.setDirectors(new HashSet<>(directors));
        }
        return films;
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        String sql = FIND_FILMS_BY_DIRECTOR;
        if ("likes".equalsIgnoreCase(sortBy)) {
            sql += ORDER_BY_LIKES;
        } else {
            sql += ORDER_BY_YEAR;
        }
        List<Film> films = findMany(sql, directorId);
        for (Film film : films) {
            List<Director> directors = directorDbStorage.findDirectorsByFilmId(film.getId());
            film.setDirectors(new HashSet<>(directors));
        }
        return films;
    }

    @Override
    public List<Film> search(String query, String by) {
        boolean searchByTitle = by.contains("title");
        boolean searchByDirector = by.contains("director");

        // Строим динамический запрос
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT f.\"FilmId\", f.\"Name\", f.\"Description\", f.\"ReleaseDate\", f.\"Duration\", f.\"RatingId\", r.\"Name\" AS \"RatingName\" ")
                .append("FROM \"Film\" f ")
                .append("LEFT JOIN \"film_director\" fd ON f.\"FilmId\" = fd.\"FilmId\" ")
                .append("LEFT JOIN \"directors\" d ON fd.\"DirectorId\" = d.\"DirectorId\" ")
                .append("LEFT JOIN \"Rating\" r ON f.\"RatingId\" = r.\"RatingId\" ")
                .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (searchByTitle) {
            sqlBuilder.append(" AND LOWER(f.\"Name\") LIKE LOWER(?) ");
            params.add("%" + query + "%");
        }
        if (searchByDirector) {
            sqlBuilder.append(" OR LOWER(d.\"Name\") LIKE LOWER(?) ");
            params.add("%" + query + "%");
        }
        sqlBuilder.append(" GROUP BY f.\"FilmId\", f.\"Name\", f.\"Description\", f.\"ReleaseDate\", f.\"Duration\", f.\"RatingId\", r.\"Name\" ");
        sqlBuilder.append(" ORDER BY (SELECT COUNT(*) FROM \"FilmLikes\" WHERE \"FilmId\" = f.\"FilmId\") DESC");

        List<Film> films = findMany(sqlBuilder.toString(), params.toArray());

        for (Film film : films) {
            List<Director> directors = directorDbStorage.findDirectorsByFilmId(film.getId());
            film.setDirectors(new HashSet<>(directors));
        }
        return films;
    }
}
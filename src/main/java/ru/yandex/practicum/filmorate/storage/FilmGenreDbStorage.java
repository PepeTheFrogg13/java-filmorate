package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FilmGenreDbStorage extends BaseRepository<FilmGenre> implements FilmGenreStorage {

    private static final String FIND_ALL_FILM_GENRES = "SELECT \"FilmGenre\".\"FilmId\",\n" +
            "  \t\t \"Genre\".*\n" +
            "    FROM \"FilmGenre\"\n" +
            "    \t INNER JOIN \"Genre\" ON \"Genre\".\"GenreId\" = \"FilmGenre\".\"GenreId\"; ";

    public FilmGenreDbStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Map<Long, List> getFilmGenres() {
        List<FilmGenre> filmGenres = findMany(FIND_ALL_FILM_GENRES);
        Map<Long, List> fimlGenresResult = new HashMap<>();
        for (FilmGenre filmGenre : filmGenres) {
            Long filmId = filmGenre.getFilmId();
            Genre genre = filmGenre.getFilmGenre();
            if (!fimlGenresResult.containsKey(filmId)) {
                List<Genre> genreList = new ArrayList<>();
                genreList.add(genre);
                fimlGenresResult.put(filmId, genreList);
            } else {
                List genreList = fimlGenresResult.get(filmId);
                genreList.add(genre);
            }
        }
        return fimlGenresResult;
    }
}

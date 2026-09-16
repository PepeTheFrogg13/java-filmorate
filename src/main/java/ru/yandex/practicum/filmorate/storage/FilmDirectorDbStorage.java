package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FilmDirectorDbStorage extends BaseRepository<FilmDirector> implements FilmDirectorStorage {

    private static final String FIND_ALL_FILM_DIRECTORS = "SELECT \"FilmDirector\".\"FilmId\",\n" +
            "\t   \"FilmDirector\".\"DirectorId\",\n" +
            "\t   \"Director\".\"Name\" \n" +
            "  from \"FilmDirector\"\n" +
            "  \t   INNER JOIN \"Director\" ON \"Director\".\"DirectorId\" = \"FilmDirector\".\"DirectorId\";\t";

    public FilmDirectorDbStorage(JdbcTemplate jdbc, RowMapper<FilmDirector> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Map<Long, List> getFilmDirectors() {
        List<FilmDirector> filmDirectors = findMany(FIND_ALL_FILM_DIRECTORS);
        Map<Long, List> filmDirectorsResult = new HashMap<>();
        for (FilmDirector filmDirector : filmDirectors) {
            Long filmId = filmDirector.getFilmId();
            Director director = filmDirector.getDirector();
            if (!filmDirectorsResult.containsKey(filmId)) {
                List<Director> directorList = new ArrayList<>();
                directorList.add(director);
                filmDirectorsResult.put(filmId, directorList);
            } else {
                List directorList = filmDirectorsResult.get(filmId);
                directorList.add(director);
            }
        }
        return filmDirectorsResult;
    }
}

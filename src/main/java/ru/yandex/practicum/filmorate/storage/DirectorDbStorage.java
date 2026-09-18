package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {

    private static final String FIND_ALL_DIRECTORS = "SELECT \"DirectorId\", \"Name\" FROM \"Director\"";
    private static final String FIND_BY_ID = "SELECT \"DirectorId\", \"Name\" FROM \"Director\" WHERE \"DirectorId\" = ?";
    private static final String FIND_DIRECTORS_BY_FILM = "SELECT d.\"DirectorId\", d.\"Name\" " +
            "FROM \"Director\" d " +
            "JOIN \"FilmDirector\" fd ON d.\"DirectorId\" = fd.\"DirectorId\" " +
            "WHERE fd.\"FilmId\" = ?";

    private static final String INSERT_DIRECTOR = "INSERT INTO \"Director\" (\"Name\") VALUES (?)";
    private static final String INSERT_DIRECTOR_INTO_FILM = "MERGE INTO \"FilmDirector\" (\"FilmId\",\"DirectorId\") KEY (\"FilmId\",\"DirectorId\") VALUES (?,?);";

    private static final String UPDATE_DIRECTOR = "UPDATE \"Director\" SET \"Name\" = ? WHERE \"DirectorId\" = ?";

    private static final String DELETE_DIRECTOR = "DELETE FROM \"Director\" WHERE \"DirectorId\" = ?";
    private static final String DELETE_DIRECTOR_FROM_FILM = "DELETE FROM \"FilmDirector\" WHERE \"FilmId\" = ?";


    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_DIRECTORS);
    }

    public Optional<Director> findById(Long id) {
        return findOne(FIND_BY_ID, id);
    }

    public Director save(Director director) {
        Long id = insert(INSERT_DIRECTOR, director.getName());
        director.setId(id);
        return director;
    }

    public Optional<Director> update(Director director) {
        Long id = director.getId();
        Optional<Director> directorOptional = findById(id);
        if (directorOptional.isEmpty()) {
            throw new IdNotFoundException("Режиссёр с id " + id + " не найден");
        }
        update(UPDATE_DIRECTOR, director.getName(), id);
        return findById(id);
    }

    public void delete(Long id) {
        delete(DELETE_DIRECTOR, id);
    }

    public List<Director> findDirectorsByFilmId(Long filmId) {
        return findMany(FIND_DIRECTORS_BY_FILM, filmId);
    }

    public void addDirectorsToFilm(Long filmId, Set<Director> directors) {
        for (Director director : directors) {
            insert(INSERT_DIRECTOR_INTO_FILM, filmId, director.getId());
        }
    }

    public void removeDirectorsFromFilm(Long filmId) {
        delete(DELETE_DIRECTOR_FROM_FILM, filmId);
    }
}

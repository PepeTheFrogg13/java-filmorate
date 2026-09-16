package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class DirectorDbStorageTest {

    @Autowired
    private DirectorDbStorage directorDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM \"FilmDirector\"");
        jdbcTemplate.execute("DELETE FROM \"Director\"");
        jdbcTemplate.execute("ALTER TABLE \"Director\" ALTER COLUMN \"DirectorId\" RESTART WITH 1");
    }

    @Test
    void shouldSaveAndFindDirector() {
        Director director = new Director();
        director.setName("Кристофер Нолан");

        Director saved = directorDbStorage.save(director);
        assertNotNull(saved.getId());

        Director found = directorDbStorage.findById(saved.getId()).get();
        assertEquals("Кристофер Нолан", found.getName());
    }

    @Test
    void shouldFindAllDirectors() {
        Director d1 = new Director();
        d1.setName("Director 1");
        directorDbStorage.save(d1);
        Director d2 = new Director();
        d2.setName("Director 2");
        directorDbStorage.save(d2);

        List<Director> all = directorDbStorage.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void shouldAddAndFindDirectorsByFilm() {
        Director d1 = new Director();
        d1.setName("Дэнни Бойл");
        directorDbStorage.save(d1);
        Director d2 = new Director();
        d2.setName("Квентин Тарантино");
        directorDbStorage.save(d2);

        Long filmId = insertFilm("Test Film");

        directorDbStorage.addDirectorsToFilm(filmId, Set.of(d1, d2));

        List<Director> directors = directorDbStorage.findDirectorsByFilmId(filmId);
        assertEquals(2, directors.size());
        List<String> names = directors.stream().map(Director::getName).toList();
        assertTrue(names.contains("Дэнни Бойл"));
        assertTrue(names.contains("Квентин Тарантино"));
    }

    @Test
    void shouldRemoveDirectorsFromFilm() {
        Director d = new Director();
        d.setName("Director");
        directorDbStorage.save(d);
        Long filmId = insertFilm("Film");
        directorDbStorage.addDirectorsToFilm(filmId, Set.of(d));

        List<Director> before = directorDbStorage.findDirectorsByFilmId(filmId);
        assertEquals(1, before.size());

        directorDbStorage.removeDirectorsFromFilm(filmId);
        List<Director> after = directorDbStorage.findDirectorsByFilmId(filmId);
        assertTrue(after.isEmpty());
    }

    private Long insertFilm(String name) {
        String sql = "INSERT INTO \"Film\" (\"Name\", \"Description\", \"ReleaseDate\", \"Duration\") VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"FilmId"});
            ps.setString(1, name);
            ps.setString(2, "desc");
            ps.setDate(3, java.sql.Date.valueOf("2020-01-01"));
            ps.setInt(4, 120);
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}

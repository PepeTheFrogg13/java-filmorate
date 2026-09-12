package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class DirectorServiceTest {

    @Autowired
    private DirectorService directorService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM \"film_director\"");
        jdbcTemplate.execute("DELETE FROM \"directors\"");
        jdbcTemplate.execute("ALTER TABLE \"directors\" ALTER COLUMN \"DirectorId\" RESTART WITH 1");
    }

    @Test
    void shouldCreateAndGetDirector() {
        Director director = new Director();
        director.setName("Кристофер Нолан");

        Director created = directorService.createDirector(director);
        assertNotNull(created.getId());

        Director found = directorService.getDirectorById(created.getId());
        assertEquals("Кристофер Нолан", found.getName());
    }

    @Test
    void shouldGetAllDirectors() {
        Director d1 = new Director();
        d1.setName("A");
        directorService.createDirector(d1);

        Director d2 = new Director();
        d2.setName("B");
        directorService.createDirector(d2);

        List<Director> all = directorService.getAllDirectors();
        assertEquals(2, all.size());

        List<String> names = all.stream().map(Director::getName).toList();
        assertTrue(names.contains("A"));
        assertTrue(names.contains("B"));
    }

    @Test
    void shouldUpdateDirector() {
        Director oldDirector = new Director();
        oldDirector.setName("Old");
        Director created = directorService.createDirector(oldDirector);

        Director newDirector = new Director();
        newDirector.setName("New");
        Director updated = directorService.createDirector(newDirector);
        assertEquals("New", updated.getName());
    }

    @Test
    void shouldDeleteDirector() {
        Director d = new Director();
        d.setName("Имя");
        Director director = directorService.createDirector(d);

        directorService.deleteDirector(director.getId());
        assertThrows(IdNotFoundException.class, () -> directorService.getDirectorById(director.getId()));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        Director director = new Director();
        director.setName("");
        assertThrows(ValidationException.class, () -> directorService.createDirector(director));
        director.setName("   ");
        assertThrows(ValidationException.class, () -> directorService.createDirector(director));
    }
}

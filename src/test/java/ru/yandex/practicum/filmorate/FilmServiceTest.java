package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private DirectorService directorService;

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM \"FilmLikes\"");
        jdbcTemplate.execute("DELETE FROM \"FilmGenre\"");
        jdbcTemplate.execute("DELETE FROM \"film_director\"");
        jdbcTemplate.execute("DELETE FROM \"Film\"");
        jdbcTemplate.execute("DELETE FROM \"directors\"");
        jdbcTemplate.execute("ALTER TABLE \"directors\" ALTER COLUMN \"DirectorId\" RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE \"Film\" ALTER COLUMN \"FilmId\" RESTART WITH 1");
    }

    private Film createTestFilm(String name, LocalDate releaseDate, Director... directors) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("desc");
        film.setReleaseDate(releaseDate);
        film.setDuration(120);
        if (directors != null && directors.length > 0) {
            film.setDirectors(Set.of(directors));
        }
        // Рейтинг обязателен, чтобы избежать NPE
        Rating rating = new Rating();
        rating.setId(1L);   // предполагаем, что в БД есть запись с id=1
        rating.setName("G");
        film.setRating(rating);
        return filmDbStorage.createFilm(film);
    }

    @Test
    void shouldGetFilmsByDirectorSortedByYear() {
        Director director = new Director();
        director.setName("Нолан");
        directorService.createDirector(director);

        createTestFilm("Начало", LocalDate.of(2010, 7, 16), director);
        createTestFilm("Довод", LocalDate.of(2020, 8, 26), director);

        List<FilmDto> result = filmService.getFilmsByDirector(director.getId(), "year");
        assertEquals(2, result.size());
        assertEquals("Начало", result.get(0).getName());
        assertEquals("Довод", result.get(1).getName());
    }

    @Test
    void shouldThrowWhenSortByInvalid() {
        Director director = new Director();
        director.setName("Нолан");
        directorService.createDirector(director);

        assertThrows(ValidationException.class,
                () -> filmService.getFilmsByDirector(director.getId(), "invalid"));
    }
}

package ru.yandex.practicum.filmorate.storage;


import java.util.List;
import java.util.Map;

public interface FilmGenreStorage {

    Map<Long, List> getFilmGenres();

}

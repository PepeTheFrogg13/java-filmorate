package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FilmGenre {

    private Long id;

    @NotNull
    private Film film;

    @NotNull
    private Genre genre;

}

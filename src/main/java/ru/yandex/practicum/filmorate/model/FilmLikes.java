package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FilmLikes {

    private Long id;

    @NotNull
    private Film film;

    @NotNull
    private User user;

}

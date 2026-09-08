package ru.yandex.practicum.filmorate.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

@Data
public class FilmNewRequest {
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotNull
    @Size(max = 200, message = "Описание не может быть больше 200 символов")
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    //Ссылка на рейтинг
    private RatingInsert mpa;

    //перечень жанров
    private ArrayList<GenreInsert> genres = new ArrayList<>();

    private Set<Director> directors;
}

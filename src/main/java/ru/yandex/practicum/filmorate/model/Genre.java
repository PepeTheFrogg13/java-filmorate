package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Genre {

    private Long id;

    @NotNull
    private String name;

}

package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewNewRequest {

    @NotBlank(message = "Текст отзыва не должен быть пустым")
    @Size(max = 2000, message = "Текст отзыва не может быть длиннее 2000 символов")
    private String content;

    @NotNull(message = "Необходимо указать тип отзыва")
    private Boolean isPositive;

    @NotNull(message = "Необходимо указать пользователя")
    private Long userId;

    @NotNull(message = "Необходимо указать фильм")
    private Long filmId;
}

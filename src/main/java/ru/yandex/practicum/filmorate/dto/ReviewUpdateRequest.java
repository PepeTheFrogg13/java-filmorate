package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewUpdateRequest {
    @NotNull(message = "Отзыв должен быть указан")
    private Long reviewId;

    @NotBlank(message = "Текст отзыва не должен быть пустым")
    @Size(max = 2000, message = "Текст отзыва не может быть длиннее 2000 символов")
    private String content;

    @NotNull(message = "Необходимо указать тип отзыва")
    private Boolean isPositive;

}

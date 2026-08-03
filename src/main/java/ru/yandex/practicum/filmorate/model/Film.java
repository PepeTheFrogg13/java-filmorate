package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;

/**
 * Film.
 */
@Data
public class Film {

    private static LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    private Long id;

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

    //Храним id лайкнувших пользователей
    private HashSet<Long> likes;

    public void validate() {
        if (name.isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (description.length() > 200) {
            throw new ValidationException("Описание не может быть больше 200 символов");
        }

        if (releaseDate.isBefore(MIN_DATE)) {
            String message = "Дата фильма не может быть раньше " + MIN_DATE;
            throw new ValidationException(message);
        }

        if (duration < 1) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

}

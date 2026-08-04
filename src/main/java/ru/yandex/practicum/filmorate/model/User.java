package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;


@Data
public class User {

    private Long id;

    @NotNull
    @Email(message = "Формат электронной почты не соответствует")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;


    private String name;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Дата рождения не может быть позже текущего времени")
    private LocalDate birthday;

    //Друзья
    private HashSet<Long> friends = new HashSet<>();

    public void validate() {
        if (email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (login.contains(" ") || login.isBlank()) {
            String message = "Логин не может быть пустым и содержать пробелы";
            throw new ValidationException(message);
        }

        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

}

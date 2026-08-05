package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

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


}

package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserFriends {

    private Long id;

    @NotNull
    private User sender;

    @NotNull
    private User recipient;

    @NotNull
    private FriendStatus friendStatus;

}

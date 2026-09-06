package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserNewRequest;
import ru.yandex.practicum.filmorate.dto.UserUpdateRequest;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("UserID"));
        user.setEmail(rs.getString("Email"));
        user.setLogin(rs.getString("Login"));
        user.setName(rs.getString("Name"));
        user.setBirthday(rs.getDate("Birthday").toLocalDate());
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setLogin(user.getLogin());
        userDto.setName(user.getName());
        userDto.setBirthday(user.getBirthday());
        userDto.setFriendlist(user.getFriendlist());
        return userDto;
    }

    public static User mapToUser(UserNewRequest userNewRequest) {
        User user = new User();
        user.setEmail(userNewRequest.getEmail());
        user.setLogin(userNewRequest.getLogin());
        user.setName(userNewRequest.getName());
        user.setBirthday(userNewRequest.getBirthday());
        return user;
    }

    public static User mapToUser(UserUpdateRequest userUpdateRequest) {
        User user = new User();
        user.setId(userUpdateRequest.getId());
        user.setEmail(userUpdateRequest.getEmail());
        user.setLogin(userUpdateRequest.getLogin());
        user.setName(userUpdateRequest.getName());
        user.setBirthday(userUpdateRequest.getBirthday());
        return user;

    }
}

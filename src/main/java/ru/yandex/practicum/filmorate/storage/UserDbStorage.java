package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;


@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_USER_ALL = "SELECT * FROM \"User\";";
    private static final String FIND_USER_BY_ID = "SELECT * FROM \"User\" WHERE \"User\".\"UserID\" = ?;";
    private static final String FIND_USER_FRIENDS = "SELECT \"User\".* \n" +
            "  FROM \"UserFriends\" \n" +
            "  \t   INNER JOIN \"User\" ON \"User\".\"UserID\" = \"UserFriends\".\"UserRecipientId\" \n" +
            " WHERE \"UserFriends\".\"UserSenderId\" = ?\n" +
            " UNION ALL\n" +
            "SELECT \"User\".* \n" +
            "  FROM \"UserFriends\" \n" +
            "  \t   INNER JOIN \"User\" ON \"User\".\"UserID\" = \"UserFriends\".\"UserSenderId\"  \n" +
            " WHERE \"UserFriends\".\"UserRecipientId\" = ? \n" +
            "   AND \"UserFriends\".\"StatusId\" = 2";

    private static final String INSERT_USER = "INSERT INTO \"User\" (\"Email\",\"Login\",\"Name\",\"Birthday\") VALUES (?,?,?,?);";

    private static final String UPDATE_USER = "UPDATE \"User\" SET \"Email\" = ?, \"Login\" = ?, \"Name\" = ?, \"Birthday\" = ? WHERE \"UserID\" = ?;";

    private static final String DELETE_USER = "DELETE FROM \"User\" WHERE \"UserID\" = ?;";

    private static final String INSERT_FRIEND_REQUSET = "INSERT INTO \"UserFriends\" (\"UserSenderId\",\"UserRecipientId\",\"StatusId\") VALUES (?,?,1);";

    private static final String DELETE_FRIEND = "DELETE FROM \"UserFriends\" WHERE (\"UserSenderId\" = ? AND \"UserRecipientId\" = ?) OR (\"UserRecipientId\" = ? AND \"UserSenderId\" = ?);";

    private static final String CONFIRM_FRIEND = "UPDATE \"UserFriends\" SET \"StatusId\" = 2 WHERE (\"UserSenderId\" = ? AND \"UserRecipientId\" = ?)";

    private static final String GET_USERS_BY_FILM = " SELECT \"User\".*\n" +
            "   FROM \"User\" \n" +
            "   \t\tINNER JOIN \"FilmLikes\" ON \"FilmLikes\".\"UserID\"  = \"User\".\"UserID\" \n" +
            "  WHERE \"FilmLikes\".\"FilmId\"  = ?;";


    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return findOne(FIND_USER_BY_ID, id);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_USER_ALL);
    }

    @Override
    public Collection<User> findFriends(Long id) {
        return findMany(FIND_USER_FRIENDS, id, id);
    }

    @Override
    public User createUser(User user) {
        Long id = insert(INSERT_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public Optional<User> updateUser(User user) {
        update(UPDATE_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getUserById(user.getId());
    }

    @Override
    public User deleteUser(User user) {
        delete(DELETE_USER, user.getId());
        return user;
    }

    @Override
    public Optional<User> changeFriend(Long id, Long friendId, Integer option) {
        switch (option) {
            case 1:
                insert(INSERT_FRIEND_REQUSET, id, friendId);
                break;
            case 2:
                update(DELETE_FRIEND, id, friendId, friendId, id);
                break;
            case 3:
                update(CONFIRM_FRIEND, id, friendId);
                break;
            default:
                break;
        }
        return getUserById(id);
    }

    @Override
    public Collection<User> findLikesByFilm(Long id) {
        return findMany(GET_USERS_BY_FILM, id);
    }
}

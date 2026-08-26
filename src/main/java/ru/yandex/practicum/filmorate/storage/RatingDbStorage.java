package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Repository
public class RatingDbStorage extends BaseRepository<Rating> implements RatingStorage {

    private static final String RATING_ALL = "SELECT * FROM \"Rating\";";
    private static final String RATING_BY_ID = "SELECT * FROM \"Rating\" WHERE \"Rating\".\"RatingId\" = ?;";

    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Rating> findAll() {
        return findMany(RATING_ALL);
    }

    @Override
    public Optional<Rating> findById(Long id) {
        return findOne(RATING_BY_ID, id);
    }

}

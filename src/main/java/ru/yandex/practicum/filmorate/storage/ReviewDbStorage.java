package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    private static final String FIND_BY_ID =
            "SELECT * FROM \"Review\" WHERE \"ReviewId\" = ?;";

    private static final String INSERT_REVIEW =
            "INSERT INTO \"Review\" (\"Content\", \"IsPositive\", \"UserID\", \"FilmId\", \"Useful\") " +
                    "VALUES (?, ?, ?, ?, 0);";

    private static final String UPDATE_REVIEW =
            "UPDATE \"Review\" SET \"Content\" = ?, \"IsPositive\" = ? WHERE \"ReviewId\" = ?;";

    private static final String DELETE_REVIEW =
            "DELETE FROM \"Review\" WHERE \"ReviewId\" = ?;";

    private static final String FIND_ALL_BY_FILM =
            "SELECT * FROM \"Review\" WHERE \"FilmId\" = ? ORDER BY \"Useful\" DESC LIMIT ?;";

    private static final String FIND_ALL =
            "SELECT * FROM \"Review\" ORDER BY \"Useful\" DESC LIMIT ?;";

    private static final String INSERT_LIKE =
            "MERGE INTO \"ReviewLikes\" (\"ReviewId\", \"UserID\", \"IsLike\") " +
                    "KEY (\"ReviewId\", \"UserID\") VALUES (?, ?, TRUE);";

    private static final String INSERT_DISLIKE =
            "MERGE INTO \"ReviewLikes\" (\"ReviewId\", \"UserID\", \"IsLike\") " +
                    "KEY (\"ReviewId\", \"UserID\") VALUES (?, ?, FALSE);";

    private static final String DELETE_LIKE =
            "DELETE FROM \"ReviewLikes\" WHERE \"ReviewId\" = ? AND \"UserID\" = ? AND \"IsLike\" = TRUE;";

    private static final String DELETE_DISLIKE =
            "DELETE FROM \"ReviewLikes\" WHERE \"ReviewId\" = ? AND \"UserID\" = ? AND \"IsLike\" = FALSE;";

    private static final String RECALCULATE_USEFUL =
            "UPDATE \"Review\" SET \"Useful\" = (" +
                    "    SELECT COALESCE(SUM(CASE WHEN \"IsLike\" = TRUE THEN 1 ELSE -1 END), 0) " +
                    "    FROM \"ReviewLikes\" WHERE \"ReviewId\" = ?" +
                    ") WHERE \"ReviewId\" = ?;";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review createReview(Review review) {
        Long id = insert(
                INSERT_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        );

        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        update(
                UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        return getReviewById(review.getReviewId());
    }

    @Override
    public Review deleteReview(Long reviewId) {
        Optional<Review> reviewOpt = getReviewById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new IdNotFoundException("Отзыв с id = " + reviewId + " не найден");
        }

        delete(DELETE_REVIEW, reviewId);
        return reviewOpt.get();
    }

    @Override
    public Optional<Review> getReviewById(Long reviewId) {
        return findOne(FIND_BY_ID, reviewId);
    }

    @Override
    public List<Review> getReviews(Long filmId, Integer count) {
        int limit = (count == null || count <= 0) ? 10 : count;

        if (filmId == null) {
            return findMany(FIND_ALL, limit);
        }

        return findMany(FIND_ALL_BY_FILM, filmId, limit);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        update(INSERT_LIKE, reviewId, userId);
        recalculateUseful(reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        update(INSERT_DISLIKE, reviewId, userId);
        recalculateUseful(reviewId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        update(DELETE_LIKE, reviewId, userId);
        recalculateUseful(reviewId);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        update(DELETE_DISLIKE, reviewId, userId);
        recalculateUseful(reviewId);
    }

    private void recalculateUseful(Long reviewId) {
        update(RECALCULATE_USEFUL, reviewId, reviewId);
    }
}
package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewNewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final UserDbStorage userStorage;
    private final EventStorage eventStorage;
    private final EventService eventService;
    private final ReviewService reviewService;

    @BeforeEach
    void setUp() {
        // Рейтинги
        jdbcTemplate.update("MERGE INTO \"Rating\" (\"RatingId\", \"Name\") KEY(\"RatingId\") VALUES (1, 'G')");
        jdbcTemplate.update("MERGE INTO \"Rating\" (\"RatingId\", \"Name\") KEY(\"RatingId\") VALUES (2, 'PG')");

        // Пользователи
        jdbcTemplate.update("MERGE INTO \"User\" (\"UserID\", \"Email\", \"Login\", \"Name\", \"Birthday\") KEY(\"UserID\") VALUES (1, 'u1@mail.ru', 'user1', 'User One', '1990-01-01')");
        jdbcTemplate.update("MERGE INTO \"User\" (\"UserID\", \"Email\", \"Login\", \"Name\", \"Birthday\") KEY(\"UserID\") VALUES (2, 'u2@mail.ru', 'user2', 'User Two', '1991-02-02')");
        jdbcTemplate.update("MERGE INTO \"User\" (\"UserID\", \"Email\", \"Login\", \"Name\", \"Birthday\") KEY(\"UserID\") VALUES (3, 'u3@mail.ru', 'user3', 'User Three', '1992-03-03')");

        // Фильмы (обязательно id=1)
        jdbcTemplate.update("MERGE INTO \"Film\" (\"FilmId\", \"Name\", \"Description\", \"ReleaseDate\", \"Duration\", \"RatingId\") KEY(\"FilmId\") VALUES (1, 'Film 1', 'desc', '2020-01-01', 120, 1)");
        jdbcTemplate.update("MERGE INTO \"Film\" (\"FilmId\", \"Name\", \"Description\", \"ReleaseDate\", \"Duration\", \"RatingId\") KEY(\"FilmId\") VALUES (2, 'Film 2', 'desc', '2021-01-01', 120, 2)");

        // Дружба 2 → 3 (для getFeed)
        jdbcTemplate.update("MERGE INTO \"UserFriends\" (\"UserFriendsId\", \"UserSenderId\", \"UserRecipientId\", \"StatusId\") KEY(\"UserFriendsId\") VALUES (1, 2, 3, 2)");
    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));

    }

    @Test
    public void testAllUsers() {

        Collection<User> users = userStorage.findAll();
        assertThat(users.size()).isEqualTo(3);

    }

    @Test
    public void testAddAndGetEvent() {
        eventStorage.addEvent(2L, EventType.LIKE, Operation.ADD, 1L);

        List<Event> events = eventStorage.getEventsByUserIds(List.of(2L));

        assertThat(events).anySatisfy(event -> {
            assertThat(event.getUserId()).isEqualTo(2L);
            assertThat(event.getEventType()).isEqualTo(EventType.LIKE);
            assertThat(event.getOperation()).isEqualTo(Operation.ADD);
            assertThat(event.getEntityId()).isEqualTo(1L);
        });
    }


    @Test
    void createsReviewWithZeroUseful() {
        ReviewDto review = reviewService.createReview(
                newReview("Положительный отзыв", true, 1L, 1L));

        assertThat(review.getReviewId()).isPositive();
        assertThat(review.getContent()).isEqualTo("Положительный отзыв");
        assertThat(review.getIsPositive()).isTrue();
        assertThat(review.getUserId()).isEqualTo(1L);
        assertThat(review.getFilmId()).isEqualTo(1L);
        assertThat(review.getUseful()).isZero();
    }

    @Test
    void updatesAndDeletesReview() {
        ReviewDto created = reviewService.createReview(
                newReview("Старый текст", false, 1L, 1L));

        ReviewUpdateRequest update = new ReviewUpdateRequest();
        update.setReviewId(created.getReviewId());
        update.setContent("Новый текст");
        update.setIsPositive(true);

        ReviewDto updated = reviewService.updateReview(update);
        ReviewDto deleted = reviewService.deleteReview(created.getReviewId());

        assertThat(updated.getContent()).isEqualTo("Новый текст");
        assertThat(updated.getIsPositive()).isTrue();
        assertThat(deleted.getReviewId()).isEqualTo(created.getReviewId());
        assertThatThrownBy(() -> reviewService.getReviewById(created.getReviewId()))
                .isInstanceOf(IdNotFoundException.class);
    }

    @Test
    void changesAndRemovesUserReaction() {
        ReviewDto created = reviewService.createReview(
                newReview("Отзыв", true, 1L, 1L));

        assertThat(reviewService.addLike(created.getReviewId(), 2L).getUseful()).isEqualTo(1);
        assertThat(reviewService.addDislike(created.getReviewId(), 2L).getUseful()).isEqualTo(-1);
        assertThat(reviewService.deleteDislike(created.getReviewId(), 2L).getUseful()).isZero();
        assertThat(reviewService.deleteLike(created.getReviewId(), 2L).getUseful()).isZero();
    }

    @Test
    void returnsReviewsSortedByUsefulAndLimitedByCount() {
        ReviewDto lessUseful = reviewService.createReview(
                newReview("Менее полезный", true, 1L, 1L));
        ReviewDto mostUseful = reviewService.createReview(
                newReview("Самый полезный", true, 2L, 1L));

        reviewService.addLike(mostUseful.getReviewId(), 3L);
        reviewService.addDislike(lessUseful.getReviewId(), 3L);

        List<ReviewDto> reviews = reviewService.getReviews(1L, 1);

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getReviewId()).isEqualTo(mostUseful.getReviewId());
        assertThat(reviews.get(0).getUseful()).isEqualTo(1);
    }

    private ReviewNewRequest newReview(String content, boolean positive, Long userId, Long filmId) {
        ReviewNewRequest request = new ReviewNewRequest();
        request.setContent(content);
        request.setIsPositive(positive);
        request.setUserId(userId);
        request.setFilmId(filmId);
        return request;
    }
}

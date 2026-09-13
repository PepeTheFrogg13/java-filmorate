package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewNewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(ReviewStorage reviewStorage,
                         UserStorage userStorage,
                         FilmStorage filmStorage,
                         EventStorage eventStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

    public ReviewDto createReview(ReviewNewRequest request) {
        validateUser(request.getUserId());
        validateFilm(request.getFilmId());

        Review review = ReviewMapper.mapToReview(request);
        review.setUseful(0);

        Review created = reviewStorage.createReview(review);
        eventStorage.addEvent(review.getUserId(), EventType.REVIEW, Operation.ADD, created.getReviewId());
        return ReviewMapper.mapToReviewDto(created);
    }

    public ReviewDto updateReview(ReviewUpdateRequest request) {
        Optional<Review> reviewOpt = reviewStorage.getReviewById(request.getReviewId());
        if (reviewOpt.isEmpty()) {
            throw new IdNotFoundException("Отзыв с id = " + request.getReviewId() + " не найден");
        }

        Review review = reviewOpt.get();
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());

        Optional<Review> updated = reviewStorage.updateReview(review);
        Review result = updated.orElseThrow(
                () -> new IdNotFoundException("Отзыв с id = " + request.getReviewId() + " не найден")
        );
        eventStorage.addEvent(result.getUserId(), EventType.REVIEW, Operation.UPDATE, result.getReviewId());
        return ReviewMapper.mapToReviewDto(result);
    }

    public ReviewDto deleteReview(Long reviewId) {
        Optional<Review> reviewOpt = reviewStorage.getReviewById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new IdNotFoundException("Отзыв с id = " + reviewId + " не найден");
        }

        Review deleted = reviewStorage.deleteReview(reviewId);
        eventStorage.addEvent(deleted.getUserId(), EventType.REVIEW, Operation.REMOVE, deleted.getReviewId());
        return ReviewMapper.mapToReviewDto(deleted);
    }

    public ReviewDto getReviewById(Long reviewId) {
        Optional<Review> reviewOpt = reviewStorage.getReviewById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new IdNotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
        return ReviewMapper.mapToReviewDto(reviewOpt.get());
    }

    public List<ReviewDto> getReviews(Long filmId, Integer count) {
        int size = (count == null || count <= 0) ? 10 : count;
        return reviewStorage.getReviews(filmId, size)
                .stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
    }

    public ReviewDto addLike(Long reviewId, Long userId) {
        validateUser(userId);
        validateReview(reviewId);
        reviewStorage.addLike(reviewId, userId);
        return getReviewById(reviewId);
    }

    public ReviewDto addDislike(Long reviewId, Long userId) {
        validateUser(userId);
        validateReview(reviewId);
        reviewStorage.addDislike(reviewId, userId);
        return getReviewById(reviewId);
    }

    public ReviewDto deleteLike(Long reviewId, Long userId) {
        validateUser(userId);
        validateReview(reviewId);
        reviewStorage.deleteLike(reviewId, userId);
        return getReviewById(reviewId);
    }

    public ReviewDto deleteDislike(Long reviewId, Long userId) {
        validateUser(userId);
        validateReview(reviewId);
        reviewStorage.deleteDislike(reviewId, userId);
        return getReviewById(reviewId);
    }

    private void validateUser(Long userId) {
        Optional<User> userOpt = userStorage.getUserById(userId);
        if (userOpt.isEmpty()) {
            throw new IdNotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    private void validateFilm(Long filmId) {
        Optional<Film> filmOpt = filmStorage.getFilmById(filmId);
        if (filmOpt.isEmpty()) {
            throw new IdNotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    private void validateReview(Long reviewId) {
        Optional<Review> reviewOpt = reviewStorage.getReviewById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new IdNotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
    }
}
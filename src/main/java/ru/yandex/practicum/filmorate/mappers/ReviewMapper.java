package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewNewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewUpdateRequest;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewMapper implements RowMapper<Review> {

    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("ReviewId"));
        review.setContent(rs.getString("Content"));
        review.setIsPositive(rs.getBoolean("IsPositive"));
        review.setUserId(rs.getLong("UserID"));
        review.setFilmId(rs.getLong("FilmId"));
        review.setUseful(rs.getInt("Useful"));
        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(review.getReviewId());
        reviewDto.setContent(review.getContent());
        reviewDto.setIsPositive(review.getIsPositive());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        reviewDto.setUseful(review.getUseful());
        return reviewDto;
    }

    public static Review mapToReview(ReviewNewRequest reviewNewRequest) {
        Review review = new Review();
        review.setContent(reviewNewRequest.getContent());
        review.setIsPositive(reviewNewRequest.getIsPositive());
        review.setUserId(reviewNewRequest.getUserId());
        review.setFilmId(reviewNewRequest.getFilmId());
        return review;
    }

    public static Review mapToReview(ReviewUpdateRequest reviewUpdateRequest) {
        Review review = new Review();
        review.setContent(reviewUpdateRequest.getContent());
        review.setIsPositive(reviewUpdateRequest.getIsPositive());
        review.setReviewId(reviewUpdateRequest.getReviewId());
        return review;
    }
}

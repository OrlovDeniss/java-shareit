package ru.practicum.shareit.review.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.AbstractModelMapper;
import ru.practicum.shareit.review.dto.ReviewDto;
import ru.practicum.shareit.review.model.Review;

@Component
public class ReviewModelMapper extends AbstractModelMapper<ReviewDto, Review> {

    @Override
    public ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .description(review.getDescription())
                .user(review.getUser())
                .build();
    }

    @Override
    public Review toEntity(ReviewDto reviewDto) {
        return Review.builder()
                .id(reviewDto.getId())
                .description(reviewDto.getDescription())
                .user(reviewDto.getUser())
                .build();
    }
}

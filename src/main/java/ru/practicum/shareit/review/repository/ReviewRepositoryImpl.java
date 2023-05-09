package ru.practicum.shareit.review.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.abstraction.userobject.repository.AbstractUserObjectRepository;
import ru.practicum.shareit.review.model.Review;

@Repository
public class ReviewRepositoryImpl extends AbstractUserObjectRepository<Review> implements ReviewRepository {
}

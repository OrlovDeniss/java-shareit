package ru.practicum.shareit.review.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.abstraction.userobject.service.AbstractUserObjectService;
import ru.practicum.shareit.review.dto.ReviewDto;
import ru.practicum.shareit.review.model.Review;
import ru.practicum.shareit.review.repository.ReviewRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class ReviewServiceImpl extends AbstractUserObjectService<ReviewDto, Review>
        implements ReviewService {

    protected ReviewServiceImpl(ModelMapper<ReviewDto, Review> mapper,
                                UserObjectRepository<Review> objectRepo,
                                UserRepository userRepo) {
        super(mapper, objectRepo, userRepo);
    }

    @Override
    protected ReviewRepository getObjectRepository() {
        return (ReviewRepository) getSuperObjectRepository();
    }
}

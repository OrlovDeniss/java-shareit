package ru.practicum.shareit.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.abstraction.userobject.controller.AbstractUserObjectController;
import ru.practicum.shareit.abstraction.userobject.service.UserObjectService;
import ru.practicum.shareit.review.dto.ReviewDto;
import ru.practicum.shareit.review.service.ReviewService;

@Controller
@Validated
@RequestMapping("/reviews")
public class ReviewControllerImpl extends AbstractUserObjectController<ReviewDto> implements ReviewController {

    public ReviewControllerImpl(UserObjectService<ReviewDto> service) {
        super(service);
    }

    @Override
    protected ReviewService getService() {
        return (ReviewService) getSuperService();
    }
}

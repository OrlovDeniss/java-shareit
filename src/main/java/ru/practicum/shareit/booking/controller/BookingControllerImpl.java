package ru.practicum.shareit.booking.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.abstraction.userobject.controller.AbstractUserObjectController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.util.exception.general.MethodNotAllowedException;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingControllerImpl extends AbstractUserObjectController<BookingDtoIn, BookingDtoOut>
        implements BookingController {

    private final BookingService bookingService;

    public BookingControllerImpl(BookingService bookingService) {
        super(bookingService);
        this.bookingService = bookingService;
    }

    @Override
    public BookingDtoOut get(Long objectId, Long userId) {
        throwWhenUserNotFound(userId);
        return bookingService.findById(objectId, userId);
    }

    @Override
    public BookingDtoOut patch(Long id, Map<String, Object> fields, Long userId) {
        throw new MethodNotAllowedException();
    }

    @Override
    public BookingDtoOut patch(Long id, Long userId, Boolean approved) {
        throwWhenUserNotFound(userId);
        return bookingService.patch(id, userId, approved);
    }

    @Override
    public List<BookingDtoOut> findAllByUserId(Long userId) {
        return findAllByUserId(userId, State.ALL);
    }

    @Override
    public List<BookingDtoOut> findAllByUserId(Long userId, State state) {
        return bookingService.findAllByUserIdAndState(userId, state);
    }

    @Override
    public List<BookingDtoOut> findAllByUserIdAndUserOwnsItems(Long userId, State state) {
        return bookingService.findAllByUserIdAndStateAndUserOwnsItem(userId, state);
    }

    @Override
    public void delete(Long objectId, Long userId) {
        throw new MethodNotAllowedException();
    }
}
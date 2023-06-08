package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.abstraction.controller.AbstractController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.State;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingControllerImpl extends AbstractController implements BookingController {

    private final BookingService bookingService;

    @Override
    public BookingDtoOut get(Long objectId, Long userId) {
        return bookingService.findById(objectId, userId);
    }

    @Override
    public BookingDtoOut post(BookingDtoIn bookingDtoIn, Long userId) {
        return bookingService.create(bookingDtoIn, userId);
    }

    @Override
    public BookingDtoOut put(BookingDtoIn bookingDtoIn, Long userId) {
        return bookingService.update(bookingDtoIn, userId);
    }

    @Override
    public BookingDtoOut patch(Long id, Long userId, Boolean approved) {
        return bookingService.patch(id, userId, approved);
    }

    @Override
    public List<BookingDtoOut> getAllByUserId(Integer from,
                                              Integer size,
                                              Long userId,
                                              State state) {
        throwWhenFromLessThenZero(from);
        throwWhenSizeLessThanOne(size);
        return bookingService.findAllByUserIdAndState(from, size, userId, state);
    }

    @Override
    public List<BookingDtoOut> getAllByOwnerId(Integer from,
                                               Integer size,
                                               Long userId,
                                               State state) {
        throwWhenFromLessThenZero(from);
        throwWhenSizeLessThanOne(size);
        return bookingService.findAllByOwnerIdAndState(from, size, userId, state);
    }

}
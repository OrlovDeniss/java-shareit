package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingControllerImpl implements BookingController {

    private final BookingService bookingService;

    private static final String PATH = "/bookings";

    @Override
    public BookingDtoOut get(Long bookingId, Long userId) {
        log.info("GET {}/{}, userId = {}.", PATH, bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    @Override
    public BookingDtoOut post(BookingDtoIn bookingDtoIn, Long userId) {
        log.info("POST {}, bookingDtoIn = {}, userId = {}.", PATH, bookingDtoIn, userId);
        return bookingService.create(bookingDtoIn, userId);
    }

    @Override
    public BookingDtoOut put(BookingDtoIn bookingDtoIn, Long userId) {
        log.info("PUT {}, bookingDtoIn = {}, userId = {}.", PATH, bookingDtoIn, userId);
        return bookingService.update(bookingDtoIn, userId);
    }

    @Override
    public BookingDtoOut patch(Long bookingId, Long userId, boolean approved) {
        log.info("PATCH {}/{}, userId = {}, approved = {}.", PATH, bookingId, userId, approved);
        return bookingService.patch(bookingId, userId, approved);
    }

    @Override
    public List<BookingDtoOut> getAllByUserId(Integer from,
                                              Integer size,
                                              Long userId,
                                              State state) {
        log.info("GET {}, from = {}, size = {}, userId = {}, state = {}.", PATH, from, size, userId, state);
        return bookingService.findAllByUserIdAndState(from, size, userId, state);
    }

    @Override
    public List<BookingDtoOut> getAllByOwnerId(Integer from,
                                               Integer size,
                                               Long userId,
                                               State state) {
        log.info("GET {}/owner, from = {}, size = {}, userId = {}, state = {}.", PATH, from, size, userId, state);
        return bookingService.findAllByOwnerIdAndState(from, size, userId, state);
    }

}
package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.util.exception.BookingStartEndTimeException;
import ru.practicum.shareit.util.exception.UnsupportedStateException;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingControllerImpl implements BookingController {

    private final BookingClient client;

    private static final String PATH = "/bookings";

    @Override
    public ResponseEntity<Object> get(Long bookingId, Long userId) {
        log.info("GET {}/{}, userId = {}.", PATH, bookingId, userId);
        return client.get(bookingId, userId);
    }

    @Override
    public ResponseEntity<Object> post(BookingDtoIn bookingDtoIn, Long userId) {
        throwWhenStartAfterEndOrEquals(bookingDtoIn);
        log.info("POST {}, bookingDtoIn = {}, userId = {}.", PATH, bookingDtoIn, userId);
        return client.post(bookingDtoIn, userId);
    }

    @Override
    public ResponseEntity<Object> put(BookingDtoIn bookingDtoIn, Long userId) {
        throwWhenStartAfterEndOrEquals(bookingDtoIn);
        log.info("PUT {}, bookingDtoIn = {}, userId = {}.", PATH, bookingDtoIn, userId);
        return client.put(bookingDtoIn, userId);
    }

    @Override
    public ResponseEntity<Object> patch(Long bookingId, Long userId, boolean approved) {
        log.info("PATCH {}/{}, userId = {}, approved = {}.", PATH, bookingId, userId, approved);
        return client.patch(bookingId, userId, approved);
    }

    @Override
    public ResponseEntity<Object> getUserBookings(Integer from,
                                                  Integer size,
                                                  Long userId,
                                                  String state) {
        State s = State.from(state).orElseThrow(UnsupportedStateException::new);
        log.info("GET {}, from = {}, size = {}, userId = {}, state = {}.", PATH, from, size, userId, s);
        return client.getUserBookings(from, size, userId, s);
    }

    @Override
    public ResponseEntity<Object> getOwnerBookings(Integer from,
                                                   Integer size,
                                                   Long userId,
                                                   String state) {
        State s = State.from(state).orElseThrow(UnsupportedStateException::new);
        log.info("GET {}/owner, from = {}, size = {}, userId = {}, state = {}.", PATH, from, size, userId, s);
        return client.getOwnerBookings(from, size, userId, s);
    }

    private void throwWhenStartAfterEndOrEquals(BookingDtoIn dtoIn) {
        if (dtoIn.getStart().isAfter(dtoIn.getEnd()) || dtoIn.getStart().equals(dtoIn.getEnd())) {
            throw new BookingStartEndTimeException();
        }
    }

}
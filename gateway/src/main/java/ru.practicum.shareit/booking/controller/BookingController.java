package ru.practicum.shareit.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public interface BookingController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";
    String FROM = "0";
    String SIZE = "10";
    String STATE_NAME = "state";
    String STATE_VALUE = "ALL";

    @GetMapping("{id}")
    ResponseEntity<Object> get(@PathVariable("id") @Positive Long bookingId,
                               @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PostMapping
    ResponseEntity<Object> post(@Valid @RequestBody BookingDtoIn in,
                                @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PutMapping
    ResponseEntity<Object> put(@Valid @RequestBody BookingDtoIn in,
                               @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PatchMapping("{id}")
    ResponseEntity<Object> patch(@PathVariable("id") @Positive Long bookingId,
                                 @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                                 @RequestParam boolean approved);

    @GetMapping
    ResponseEntity<Object> getUserBookings(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = SIZE) @Positive Integer size,
                                           @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                                           @RequestParam(value = STATE_NAME, defaultValue = STATE_VALUE) String state);

    @GetMapping("owner")
    ResponseEntity<Object> getOwnerBookings(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = SIZE) @Positive Integer size,
                                            @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                                            @RequestParam(value = STATE_NAME, defaultValue = STATE_VALUE) String state);

}
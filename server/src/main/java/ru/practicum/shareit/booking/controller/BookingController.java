package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import javax.validation.constraints.Positive;
import java.util.List;

public interface BookingController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";
    String FROM = "0";
    String SIZE = "10";
    String STATE = "state";
    String STATE_DEFAULT = "ALL";

    @GetMapping("{id}")
    BookingDtoOut get(@PathVariable("id") Long bookingId,
                      @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @PostMapping
    BookingDtoOut post(@RequestBody BookingDtoIn in,
                       @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PutMapping
    BookingDtoOut put(@RequestBody BookingDtoIn in,
                      @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @PatchMapping("{id}")
    BookingDtoOut patch(@PathVariable("id") Long bookingId,
                        @RequestHeader(value = X_SHARER_USER_ID) Long userId,
                        @RequestParam boolean approved);

    @GetMapping
    List<BookingDtoOut> getAllByUserId(@RequestParam(defaultValue = FROM) Integer from,
                                       @RequestParam(defaultValue = SIZE) Integer size,
                                       @RequestHeader(value = X_SHARER_USER_ID) Long userId,
                                       @RequestParam(value = STATE, defaultValue = STATE_DEFAULT) State state);

    @GetMapping("owner")
    List<BookingDtoOut> getAllByOwnerId(@RequestParam(defaultValue = FROM) Integer from,
                                        @RequestParam(defaultValue = SIZE) Integer size,
                                        @RequestHeader(value = X_SHARER_USER_ID) Long userId,
                                        @RequestParam(value = STATE, defaultValue = STATE_DEFAULT) State state);

}
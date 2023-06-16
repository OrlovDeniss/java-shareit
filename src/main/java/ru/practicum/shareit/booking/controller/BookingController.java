package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.state.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

public interface BookingController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";
    String FROM = "0";
    String SIZE = "10";
    String STATE = "state";
    String STATE_DEFAULT = "ALL";

    @GetMapping("{id}")
    BookingDtoOut get(@PathVariable("id") @Positive Long bookingId,
                      @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PostMapping
    BookingDtoOut post(@Valid @RequestBody BookingDtoIn in,
                       @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PutMapping
    BookingDtoOut put(@Valid @RequestBody BookingDtoIn in,
                      @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PatchMapping("{id}")
    BookingDtoOut patch(@PathVariable("id") @Positive Long bookingId,
                        @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                        @RequestParam boolean approved);

    @GetMapping
    List<BookingDtoOut> getAllByUserId(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = SIZE) @Positive Integer size,
                                       @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                                       @RequestParam(value = STATE, defaultValue = STATE_DEFAULT) State state);

    @GetMapping("owner")
    List<BookingDtoOut> getAllByOwnerId(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = SIZE) @Positive Integer size,
                                        @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                                        @RequestParam(value = STATE, defaultValue = STATE_DEFAULT) State state);

}
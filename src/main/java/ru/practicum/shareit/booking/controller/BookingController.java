package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.state.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

public interface BookingController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";

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
                        @RequestParam Boolean approved);

    @GetMapping
    List<BookingDtoOut> getAllByUserId(@RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                                       @RequestParam(value = "state",
                                               required = false,
                                               defaultValue = "ALL") State state);

    @GetMapping("owner")
    List<BookingDtoOut> getAllByOwnerId(@RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                                        @RequestParam(value = "state",
                                                required = false,
                                                defaultValue = "ALL") State state);

}
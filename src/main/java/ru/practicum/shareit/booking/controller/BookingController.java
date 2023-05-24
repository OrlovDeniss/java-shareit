package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.abstraction.userobject.controller.UserObjectController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.state.State;

import javax.validation.constraints.Positive;
import java.util.List;

public interface BookingController extends UserObjectController<BookingDtoIn, BookingDtoOut> {

    @PatchMapping(value = "{id}", params = {"approved"})
    BookingDtoOut patch(@PathVariable("id") @Positive Long bookingId,
                        @RequestHeader(value = USER_ID) @Positive Long userId,
                        @RequestParam Boolean approved);

    @GetMapping(params = "state")
    List<BookingDtoOut> findAllByUserId(@RequestHeader(value = USER_ID) @Positive Long userId,
                                        @RequestParam(value = "state",
                                                required = false,
                                                defaultValue = "ALL") State state);

    @GetMapping("owner")
    List<BookingDtoOut> findAllByUserIdAndUserOwnsItems(@RequestHeader(value = USER_ID) @Positive Long userId,
                                                        @RequestParam(value = "state",
                                                                required = false,
                                                                defaultValue = "ALL") State state);

}
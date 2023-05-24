package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.abstraction.userobject.service.UserObjectService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.state.State;

import java.util.List;

public interface BookingService extends UserObjectService<BookingDtoIn, BookingDtoOut> {

    BookingDtoOut findById(Long bookingId, Long userId);

    BookingDtoOut patch(Long bookingId, Long userId, Boolean approved);

    List<BookingDtoOut> findAllByUserIdAndState(Long userId, State state);

    List<BookingDtoOut> findAllByUserIdAndStateAndUserOwnsItem(Long userId, State state);

}
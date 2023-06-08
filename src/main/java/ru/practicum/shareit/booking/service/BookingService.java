package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.abstraction.userobject.UserObjectService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.state.State;

import java.util.List;

public interface BookingService extends UserObjectService<BookingDtoIn, BookingDtoOut> {

    BookingDtoOut patch(Long bookingId, Long userId, Boolean approved);

    List<BookingDtoOut> findAllByUserIdAndState(Integer from, Integer size, Long userId, State state);

    List<BookingDtoOut> findAllByOwnerIdAndState(Integer from, Integer size, Long userId, State state);

}
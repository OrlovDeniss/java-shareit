package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {

    BookingDtoOut create(BookingDtoIn bookingDtoIn, Long userId);

    BookingDtoOut update(BookingDtoIn bookingDtoIn, Long userId);

    BookingDtoOut findById(Long objectId, Long userId);

    BookingDtoOut patch(Long bookingId, Long userId, boolean approved);

    List<BookingDtoOut> findAllByUserIdAndState(Integer from, Integer size, Long userId, State state);

    List<BookingDtoOut> findAllByOwnerIdAndState(Integer from, Integer size, Long userId, State state);

}
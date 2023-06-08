package ru.practicum.shareit.abstraction.userobject;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;

class BookingAbstractUserObjectServiceTest extends AbstractUserObjectServiceTest<BookingDtoIn, BookingDtoOut, Booking> {

    private static final Long BOOKING_ID = 1L;
    private static final LocalDateTime BOOKING_START = LocalDateTime.of(2024, 1, 1, 1, 1);
    private static final LocalDateTime BOOKING_END = LocalDateTime.of(2025, 1, 1, 1, 1);
    private static final Status BOOKING_STATUS = Status.APPROVED;

    @Override
    protected Booking getEntity() {
        return Booking.builder()
                .id(BOOKING_ID)
                .start(BOOKING_START)
                .end(BOOKING_END)
                .status(BOOKING_STATUS)
                .build();
    }

    @Override
    protected BookingDtoIn getDtoIn() {
        return BookingDtoIn.builder()
                .id(BOOKING_ID)
                .start(BOOKING_START)
                .end(BOOKING_END)
                .itemId(4L)
                .build();
    }

    @Override
    protected BookingDtoOut getDtoOut() {
        return BookingDtoOut.builder()
                .id(BOOKING_ID)
                .start(BOOKING_START)
                .end(BOOKING_END)
                .item(new ItemDtoShort())
                .booker(new UserDtoShort())
                .status(BOOKING_STATUS)
                .build();
    }


}

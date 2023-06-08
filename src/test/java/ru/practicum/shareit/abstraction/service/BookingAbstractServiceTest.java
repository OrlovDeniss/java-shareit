package ru.practicum.shareit.abstraction.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Map;

class BookingAbstractServiceTest extends AbstractServiceTest<Booking> {

    private static final Long BOOKING_ID = 1L;
    private static final LocalDateTime BOOKING_START = LocalDateTime.of(2024, 1, 1, 1, 1);
    private static final LocalDateTime BOOKING_END = LocalDateTime.of(2025, 1, 1, 1, 1);
    private static final Status STATUS = Status.APPROVED;

    @Override
    protected Booking getEntity() {
        return Booking.builder()
                .id(BOOKING_ID)
                .start(BOOKING_START)
                .end(BOOKING_END)
                .status(STATUS)
                .build();
    }

    @Override
    protected Booking getReference() {
        return Booking.builder()
                .id(BOOKING_ID)
                .start(BOOKING_START)
                .end(BOOKING_END)
                .status(STATUS)
                .build();
    }

    @Override
    protected Map<String, Object> getNewFields() {
        return Map.of();
    }

}
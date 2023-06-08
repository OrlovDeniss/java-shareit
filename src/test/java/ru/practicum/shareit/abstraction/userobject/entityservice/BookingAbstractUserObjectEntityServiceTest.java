package ru.practicum.shareit.abstraction.userobject.entityservice;

import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

public class BookingAbstractUserObjectEntityServiceTest extends AbstractUserObjectEntityServiceTest<Booking> {

    @Override
    protected Booking getEntity() {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023,1,1,1,1,1))
                .end(LocalDateTime.of(2024,2,2,2,2,2))
                .build();
    }

}
package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.abstraction.mapper.AbstractModelMapper;

@Component
public class BookingModelMapper extends AbstractModelMapper<BookingDto, Booking> {

    @Override
    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getUser())
                .status(booking.getStatus())
                .build();
    }

    @Override
    public Booking toEntity(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItem())
                .user(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .build();
    }
}
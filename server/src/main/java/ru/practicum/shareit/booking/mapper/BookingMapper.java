package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.mapper.ItemMapper;

@Mapper(uses = ItemMapper.class)
public interface BookingMapper extends ModelMapper<BookingDtoIn, BookingDtoOut, Booking> {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    BookingDtoShort toDtoShort(BookingShort booking);

}
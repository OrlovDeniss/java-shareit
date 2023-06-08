package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingModelMapperTest extends AbstractModelMapperTest<BookingDtoIn, BookingDtoOut, Booking> {

    private static final Long BOOKING_ID = 1L;
    private static final Long ITEM_ID = 2L;
    private static final Long USER_ID = 3L;
    private static final LocalDateTime START = LocalDateTime.of(2077, 1, 1, 1, 1, 1);
    private static final LocalDateTime END = LocalDateTime.of(2078, 1, 1, 1, 1, 1);
    private static final String ITEM_NAME = "item_name";
    private static final String ITEM_DESCRIPTION = "item_description";
    private static final boolean AVAILABLE = true;
    private static final Status STATUS = Status.APPROVED;

    final User user = User.builder()
            .id(USER_ID)
            .name("user")
            .email("user@user.com")
            .build();

    final Item item = Item.builder()
            .id(ITEM_ID)
            .name(ITEM_NAME)
            .description(ITEM_DESCRIPTION)
            .available(AVAILABLE)
            .user(user)
            .build();

    final UserDtoShort userDtoShort = UserDtoShort.builder()
            .id(USER_ID)
            .build();

    final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(ITEM_ID)
            .name(ITEM_NAME)
            .description(ITEM_DESCRIPTION)
            .available(AVAILABLE)
            .ownerId(USER_ID)
            .build();

    final Booking booking = Booking.builder()
            .id(BOOKING_ID)
            .start(START)
            .end(END)
            .item(item)
            .user(user)
            .status(STATUS)
            .build();

    final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .id(BOOKING_ID)
            .start(START)
            .end(END)
            .itemId(ITEM_ID)
            .build();

    final BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
            .id(BOOKING_ID)
            .start(START)
            .end(END)
            .item(itemDtoShort)
            .booker(userDtoShort)
            .status(STATUS)
            .build();

    @Override
    protected Booking getEntity() {
        return booking;
    }

    @Override
    protected BookingDtoIn getDtoIn() {
        return bookingDtoIn;
    }

    @Override
    protected BookingDtoOut getDtoOut() {
        return bookingDtoOut;
    }

    @Test
    @Override
    void toEntityTest() {
        Booking booking1 = mapper.toEntity(getDtoIn());
        booking1.setStatus(STATUS); // service layer responsibility
        assertEquals(getEntity(), booking1);
    }
}
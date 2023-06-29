package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingModelMapperTest extends AbstractModelMapperTest<BookingDtoIn, BookingDtoOut, Booking> {

    private final BookingShort bookingShort = new SpelAwareProxyProjectionFactory().createProjection(BookingShort.class);

    private final Long userId = generator.nextLong();
    private final String userName = generator.nextObject(String.class);
    private final String userEmail = generator.nextObject(String.class);

    private final Long bookingId = generator.nextLong();
    private final LocalDateTime bookingStart = generator.nextObject(LocalDateTime.class);
    private final LocalDateTime bookingEnd = generator.nextObject(LocalDateTime.class);
    private final Status bookingStatus = generator.nextObject(Status.class);

    private final Long itemId = generator.nextLong();
    private final String itemName = generator.nextObject(String.class);
    private final String itemDescription = generator.nextObject(String.class);
    private final Boolean itemAvailable = generator.nextBoolean();

    private final Long requestId = generator.nextLong();

    private final User user = User.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final Request request = Request.builder()
            .id(requestId)
            .build();

    private final Item item = Item.builder()
            .id(itemId)
            .name(itemName)
            .description(itemDescription)
            .available(itemAvailable)
            .user(user)
            .request(request)
            .build();

    private final UserDtoShort userDtoShort = UserDtoShort.builder()
            .id(userId)
            .build();

    private final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(itemId)
            .name(itemName)
            .description(itemDescription)
            .available(itemAvailable)
            .ownerId(userId)
            .requestId(requestId)
            .build();

    private final Booking booking = Booking.builder()
            .id(bookingId)
            .start(bookingStart)
            .end(bookingEnd)
            .item(item)
            .user(user)
            .status(bookingStatus)
            .build();

    private final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .id(bookingId)
            .start(bookingStart)
            .end(bookingEnd)
            .itemId(itemId)
            .build();

    private final BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
            .id(bookingId)
            .start(bookingStart)
            .end(bookingEnd)
            .item(itemDtoShort)
            .user(userDtoShort)
            .status(bookingStatus)
            .build();


    private final BookingDtoShort bookingDtoShort = BookingDtoShort.builder()
            .id(bookingId)
            .bookerId(userId)
            .build();

    protected BookingModelMapperTest() {
        super(BookingMapper.INSTANCE);
    }

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
        Booking b = mapper.toEntity(getDtoIn());
        b.setStatus(bookingStatus); // service layer responsibility
        assertEquals(getEntity(), b);
    }

    @Test
    void toDtoShortTest() {
        bookingShort.setId(bookingId);
        bookingShort.setItemId(itemId);
        bookingShort.setBookerId(userId);
        BookingDtoShort b = BookingMapper.INSTANCE.toDtoShort(bookingShort);
        assertEquals(bookingDtoShort, b);
    }
}
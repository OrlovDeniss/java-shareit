package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoOutJsonTest {

    @Autowired
    private JacksonTester<BookingDtoOut> jacksonTester;

    private final LocalDateTime start = LocalDateTime.of(2077, 1, 1, 1, 1, 1);
    private final LocalDateTime end = LocalDateTime.of(2078, 1, 1, 1, 1, 1);

    private final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .requestId(4L)
            .ownerId(3L)
            .build();
    private final UserDtoShort userDtoShort = UserDtoShort.builder()
            .id(1L)
            .build();

    @Test
    void bookingDtoOut_startAndEndTest() throws Exception {
        BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(itemDtoShort)
                .user(userDtoShort)
                .status(Status.APPROVED)
                .build();
        JsonContent<BookingDtoOut> jsonContent = jacksonTester.write(bookingDtoOut);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.toString());
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.toString());
    }
}
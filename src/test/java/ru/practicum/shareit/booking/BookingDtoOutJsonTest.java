package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoOutJsonTest {

    @Autowired
    JacksonTester<BookingDtoOut> jacksonTester;

    private static final LocalDateTime START = LocalDateTime.of(2077, 1, 1, 1, 1, 1);
    private static final LocalDateTime END = LocalDateTime.of(2078, 1, 1, 1, 1, 1);
    final ItemDtoShort item = new ItemDtoShort(1L, "name", "description", true, 4L, 3L);
    final UserDtoShort user = new UserDtoShort(1L);

    @Test
    void bookingDtoOut_startAndEndTest() throws Exception {
        BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
                .id(1L)
                .start(START)
                .end(END)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        JsonContent<BookingDtoOut> jsonContent = jacksonTester.write(bookingDtoOut);

        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(START.toString());
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(END.toString());
    }
}
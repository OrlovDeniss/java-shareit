package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingControllerImpl;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingControllerImpl.class)
class BookingControllerTest {

    @MockBean
    private BookingServiceImpl bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final String xSharerUserId = "X-Sharer-User-Id";
    private final String requestMapping = "/bookings";
    private final LocalDateTime start = LocalDateTime.of(2077, 1, 1, 1, 1, 1);
    private final LocalDateTime end = LocalDateTime.of(2078, 1, 1, 1, 1, 1);
    private final String itemName = "name";
    private final String itemDescription = "description";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");

    private final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .id(1L)
            .start(start)
            .end(end)
            .itemId(1L)
            .build();

    private final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(1L)
            .name(itemName)
            .description(itemDescription)
            .available(true)
            .ownerId(1L)
            .build();

    private final BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
            .id(1L)
            .start(start)
            .end(end)
            .item(itemDtoShort)
            .user(new UserDtoShort(1L))
            .status(Status.WAITING)
            .build();

    @SneakyThrows
    @Test
    void post_whenItemDtoInIsCorrect_thenReturnItemDtoOutAndIsOk() {
        Long userId = 1L;
        when(bookingService.create(bookingDtoIn, userId))
                .thenReturn(bookingDtoOut);
        mvc.perform(post(requestMapping)
                        .header(xSharerUserId, userId)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getUser().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
        verify(bookingService, times(1))
                .create(any(BookingDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void get_whenBookingIdAndUserIdIsCorrect_thenReturnBookingDtoOutAndIsOk() {
        Long bookingId = 1L;
        Long userId = 1L;
        when(bookingService.findById(bookingId, userId))
                .thenReturn(bookingDtoOut);
        mvc.perform(get(requestMapping + "/{id}", bookingId)
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getUser().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
        verify(bookingService, times(1)).findById(any(Long.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void put_whenItemDtoInIsCorrect_thenReturnItemDtoOutAndIsOk() {
        Long userId = 1L;
        when(bookingService.update(bookingDtoIn, userId))
                .thenReturn(bookingDtoOut);
        mvc.perform(put(requestMapping)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getUser().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
        verify(bookingService, times(1)).update(any(BookingDtoIn.class), anyLong());
    }

    @SneakyThrows
    @Test
    void patch_whenCorrectParams_thenReturnBookingDtoOut() {
        Long id = 1L;
        Long userId = 1L;
        when(bookingService.patch(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoOut);
        mvc.perform(patch(requestMapping + "/{id}", id)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(xSharerUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getUser().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
        verify(bookingService, times(1)).patch(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void getAllByUserId_whenFromAndSizeNull_thanDefaultFromAndSizeAndReturnBookingDtoOutAndIsOk() {
        when(bookingService.findAllByUserIdAndState(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));
        mvc.perform(get(requestMapping)
                        .header(xSharerUserId, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService, times(1))
                .findAllByUserIdAndState(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }

    @SneakyThrows
    @Test
    void getAllByOwnerId_whenFromAndSizeNull_thanDefaultFromAndSizeAndReturnBookingDtoOutAndIsOk() {
        when(bookingService.findAllByOwnerIdAndState(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));
        mvc.perform(get(requestMapping + "/owner")
                        .header(xSharerUserId, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService, times(1))
                .findAllByOwnerIdAndState(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }

}
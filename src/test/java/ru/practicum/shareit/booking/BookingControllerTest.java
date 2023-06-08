package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.controller.BookingControllerImpl;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.util.exception.general.IncorrectRequestParamException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingControllerImpl.class)
class BookingControllerTest {

    @MockBean
    BookingServiceImpl bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    private static final String X_SHARES_USER_ID = "X-Sharer-User-Id";
    private static final String REQUEST_MAPPING = "/bookings";
    private static final LocalDateTime START = LocalDateTime.of(2077, 1, 1, 1, 1, 1);
    private static final LocalDateTime END = LocalDateTime.of(2078, 1, 1, 1, 1, 1);
    private static final String ITEM_NAME = "name";
    private static final String ITEM_DESCRIPTION = "description";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");

    final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .id(1L)
            .start(START)
            .end(END)
            .itemId(1L)
            .build();

    final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(1L)
            .name(ITEM_NAME)
            .description(ITEM_DESCRIPTION)
            .available(true)
            .ownerId(1L)
            .build();

    final BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
            .id(1L)
            .start(START)
            .end(END)
            .item(itemDtoShort)
            .booker(new UserDtoShort(1L))
            .status(Status.WAITING)
            .build();

    @SneakyThrows
    @Test
    void post_whenItemDtoInIsCorrect_thenReturnItemDtoOutAndIsOk() {
        Long userId = 1L;
        when(bookingService.create(bookingDtoIn, userId))
                .thenReturn(bookingDtoOut);

        mvc.perform(post(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, userId)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        verify(bookingService, times(1))
                .create(any(BookingDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenStartIsNull_thanReturnBadRequest() {
        bookingDtoIn.setStart(null);
        Long userId = 1L;
        when(bookingService.create(bookingDtoIn, userId))
                .thenReturn(bookingDtoOut);

        mvc.perform(post(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, userId)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(bookingService, never())
                .create(any(BookingDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenStartIsPast_thanReturnBadRequest() {
        bookingDtoIn.setStart(LocalDateTime.of(2007, 9, 1, 12, 55, 0));
        Long userId = 1L;
        when(bookingService.create(bookingDtoIn, userId))
                .thenReturn(bookingDtoOut);

        mvc.perform(post(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, userId)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(bookingService, never())
                .create(any(BookingDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenEndIsNull_thanReturnBadRequest() {
        bookingDtoIn.setEnd(null);
        Long userId = 1L;
        when(bookingService.create(bookingDtoIn, userId))
                .thenReturn(bookingDtoOut);

        mvc.perform(post(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, userId)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(bookingService, never())
                .create(any(BookingDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void get_whenBookingIdAndUserIdIsCorrect_thenReturnBookingDtoOutAndIsOk() {
        Long bookingId = 1L;
        Long userId = 1L;
        when(bookingService.findById(bookingId, userId))
                .thenReturn(bookingDtoOut);
        mvc.perform(get(REQUEST_MAPPING + "/{id}", bookingId)
                        .header(X_SHARES_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        verify(bookingService, times(1)).findById(any(Long.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void put_whenItemDtoInIsCorrect_thenReturnItemDtoOutAndIsOk() {
        Long userId = 1L;

        when(bookingService.update(bookingDtoIn, userId))
                .thenReturn(bookingDtoOut);

        mvc.perform(put(REQUEST_MAPPING)
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARES_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
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

        mvc.perform(patch(REQUEST_MAPPING + "/{id}", id)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARES_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));

        verify(bookingService, times(1)).patch(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void getAllByUserId_whenFromAndSizeNull_thanDefaultFromAndSizeAndReturnBookingDtoOutAndIsOk() {
        when(bookingService.findAllByUserIdAndState(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));

        mvc.perform(get(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1))
                .findAllByUserIdAndState(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }

    @SneakyThrows
    @Test
    void getAllByUserId_whenWrongFrom_thenReturnBadRequest() {
        int from = -1;
        int size = 1;
        when(bookingService.findAllByUserIdAndState(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));

        mvc.perform(get(REQUEST_MAPPING)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectRequestParamException))
                .andExpect(result -> assertEquals("RequestParam from должен быть >= 0.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(bookingService, never())
                .findAllByUserIdAndState(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }

    @SneakyThrows
    @Test
    void getAllByUserId_whenWrongSize_thanReturnBadRequest() {
        int from = 0;
        int size = 0;
        when(bookingService.findAllByUserIdAndState(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));

        mvc.perform(get(REQUEST_MAPPING)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectRequestParamException))
                .andExpect(result -> assertEquals("RequestParam size должен быть >= 1.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(bookingService, never())
                .findAllByUserIdAndState(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }

    @SneakyThrows
    @Test
    void getAllByOwnerId_whenFromAndSizeNull_thanDefaultFromAndSizeAndReturnBookingDtoOutAndIsOk() {
        when(bookingService.findAllByOwnerIdAndState(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));

        mvc.perform(get(REQUEST_MAPPING + "/owner")
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1))
                .findAllByOwnerIdAndState(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }

    @SneakyThrows
    @Test
    void getAllByOwnerId_whenWrongFrom_thenReturnBadRequest() {
        int from = -1;
        int size = 1;
        when(bookingService.findAllByOwnerIdAndState(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));

        mvc.perform(get(REQUEST_MAPPING + "/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectRequestParamException))
                .andExpect(result -> assertEquals("RequestParam from должен быть >= 0.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(bookingService, never())
                .findAllByOwnerIdAndState(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }

    @SneakyThrows
    @Test
    void getAllByOwnerId_whenWrongSize_thanReturnBadRequest() {
        int from = 0;
        int size = 0;
        when(bookingService.findAllByOwnerIdAndState(anyInt(), anyInt(), anyLong(), eq(State.ALL)))
                .thenReturn(List.of(bookingDtoOut));

        mvc.perform(get(REQUEST_MAPPING + "/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectRequestParamException))
                .andExpect(result -> assertEquals("RequestParam size должен быть >= 1.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(bookingService, never())
                .findAllByOwnerIdAndState(any(Integer.class), any(Integer.class), any(Long.class), eq(State.ALL));
    }

}
package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestControllerImpl;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.util.exception.general.IncorrectRequestParamException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestControllerImpl.class)
class RequestControllerTest {

    @MockBean
    RequestService requestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    static final String X_SHARES_USER_ID = "X-Sharer-User-Id";

    final RequestDtoIn requestDtoIn = RequestDtoIn.builder()
            .description("description")
            .build();

    final RequestDtoOut requestDtoOut = RequestDtoOut.builder()
            .id(1L)
            .description("description")
            .created(LocalDateTime.now())
            .build();

    @SneakyThrows
    @Test
    void post_whenRequestDtoInIsCorrect_thenReturnRequestDtoOut() {
        Long userId = 1L;

        when(requestService.create(requestDtoIn, userId))
                .thenReturn(requestDtoOut);

        mvc.perform(post("/requests")
                        .header(X_SHARES_USER_ID, userId)
                        .content(mapper.writeValueAsString(requestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOut.getDescription())));

        verify(requestService, times(1))
                .create(any(RequestDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void get_whenRequestCorrect_thenReturnRequestDtoOut() {
        Long requestId = 1L;
        Long userId = 1L;

        when(requestService.findById(requestId, userId))
                .thenReturn(requestDtoOut);

        mvc.perform(get("/requests/{id}", requestId)
                        .header(X_SHARES_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOut.getDescription())));

        verify(requestService, times(1))
                .findById(any(Long.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAll_whenRequestCorrect_thenReturnRequestDtoOut() {
        long userId = 1;
        int from = 0;
        int size = 1;

        when(requestService.findAllByUserId(from, size, userId))
                .thenReturn(List.of(requestDtoOut));

        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(X_SHARES_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoOut.getDescription())));

        verify(requestService, times(1))
                .findAllByUserId(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAll_whenFromAndSizeNullTest_thenReturnListRequestsWithDefaultSize() {
        when(requestService.findAllByUserId(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(requestDtoOut));

        mvc.perform(get("/requests/all")
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(requestService, never())
                .findAllByUserId(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAll_whenWrongFromTest_thenThrowIncorrectRequestParamException() {
        int from = -1;
        int size = 1;

        when(requestService.findAllByUserId(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(requestDtoOut));

        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectRequestParamException))
                .andExpect(result -> assertEquals("RequestParam from должен быть >= 0.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(requestService, never())
                .findAllByUserId(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAll_whenWrongSizeTest_thenThrowIncorrectRequestParamException() {
        int from = 0;
        int size = 0;

        when(requestService.findAllByUserId(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(requestDtoOut));

        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectRequestParamException))
                .andExpect(result -> assertEquals("RequestParam size должен быть >= 1.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(requestService, never())
                .findAllByUserId(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAll_whenCreatedByUser_thenReturnListOfRequestsDtoOut() {
        Long requestId = 1L;
        Long userId = 1L;

        when(requestService.findAllByOwnerId(requestId))
                .thenReturn(List.of(requestDtoOut));

        mvc.perform(get("/requests", requestId)
                        .header(X_SHARES_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoOut.getDescription())));

        verify(requestService, times(1))
                .findAllByOwnerId(any(Long.class));
    }

}
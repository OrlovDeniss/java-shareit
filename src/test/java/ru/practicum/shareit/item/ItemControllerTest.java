package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.item.controller.ItemControllerImpl;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoIn;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.util.exception.general.IncorrectRequestParamException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

@WebMvcTest(controllers = ItemControllerImpl.class)
class ItemControllerTest {

    @MockBean
    ItemServiceImpl itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    private static final String X_SHARES_USER_ID = "X-Sharer-User-Id";
    private static final String REQUEST_MAPPING = "/items";

    final ItemDtoIn itemDtoIn = ItemDtoIn.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .build();

    final ItemDtoOut itemDtoOut = ItemDtoOut.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .build();

    final CommentDtoIn commentDtoIn = CommentDtoIn.builder()
            .text("text")
            .build();

    final CommentDtoOut commentDtoOut = CommentDtoOut.builder()
            .id(1L)
            .text("text")
            .authorName("user1")
            .created(LocalDateTime.now())
            .build();

    @SneakyThrows
    @Test
    void post_whenItemDtoInIsCorrect_thenReturnItemDtoOutAndIsOk() {
        Long userId = 1L;
        when(itemService.create(itemDtoIn, userId))
                .thenReturn(itemDtoOut);

        mvc.perform(post(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));

        verify(itemService, times(1))
                .create(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenNameIsBlank_thanReturnBadRequest() {
        itemDtoIn.setName(" ");
        Long userId = 1L;
        when(itemService.create(itemDtoIn, userId))
                .thenReturn(itemDtoOut);

        mvc.perform(post(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(itemService, never())
                .create(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenDescriptionIsBlank_thanReturnBadRequest() {
        itemDtoIn.setDescription(" ");
        Long userId = 1L;
        when(itemService.create(itemDtoIn, userId))
                .thenReturn(itemDtoOut);

        mvc.perform(post(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(itemService, never())
                .create(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void post_whenAvailableNull_thanReturnBadRequest() {
        itemDtoIn.setAvailable(null);
        Long userId = 1L;
        when(itemService.create(itemDtoIn, userId))
                .thenReturn(itemDtoOut);

        mvc.perform(post(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(itemService, never())
                .create(any(ItemDtoIn.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void get_whenItemIdAndUserIdIsCorrect_thenReturnItemDtoOut() {
        Long itemId = 1L;
        Long userId = 1L;
        when(itemService.findById(itemId, userId))
                .thenReturn(itemDtoOut);
        mvc.perform(get(REQUEST_MAPPING + "/{id}", itemId)
                        .header(X_SHARES_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));

        verify(itemService, times(1)).findById(any(Long.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void put_whenItemDtoInIsCorrect_thenReturnItemDtoOut() {
        Long userId = 1L;

        when(itemService.update(itemDtoIn, userId))
                .thenReturn(itemDtoOut);

        mvc.perform(put(REQUEST_MAPPING)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARES_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));

        verify(itemService, times(1)).update(any(ItemDtoIn.class), anyLong());
    }

    @SneakyThrows
    @Test
    void patch_() {
        Long id = 1L;
        Long userId = 1L;
        when(itemService.patch(id, Map.of(), userId))
                .thenReturn(itemDtoOut);

        mvc.perform(patch(REQUEST_MAPPING + "/{id}", id)
                        .content(mapper.writeValueAsString(Map.of()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARES_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));

        verify(itemService, times(1)).patch(anyLong(), anyMap(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllByUserId_whenFromAndSizeNull_thanReturnDefaultFromAndSize() {
        when(itemService.findAllByUserId(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(itemDtoOut));

        mvc.perform(get(REQUEST_MAPPING)
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemService, times(1))
                .findAllByUserId(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAllByUserId_whenWrongFrom_thenReturnBadRequest() {
        int from = -1;
        int size = 1;
        when(itemService.findAllByUserId(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(itemDtoOut));

        mvc.perform(get(REQUEST_MAPPING)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectRequestParamException))
                .andExpect(result -> assertEquals("RequestParam from должен быть >= 0.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(itemService, never())
                .findAllByUserId(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void getAllByUserId_whenWrongSize_thanReturnBadRequest() {
        int from = 0;
        int size = 0;
        when(itemService.findAllByUserId(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(itemDtoOut));

        mvc.perform(get(REQUEST_MAPPING)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IncorrectRequestParamException))
                .andExpect(result -> assertEquals("RequestParam size должен быть >= 1.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        verify(itemService, never())
                .findAllByUserId(any(Integer.class), any(Integer.class), any(Long.class));
    }

    @SneakyThrows
    @Test
    void search_whenCorrectRequest_returnItemDtoOutAndIsOk() {
        when(itemService.searchByNameOrDescription(anyInt(), anyInt(), anyString()))
                .thenReturn(List.of(itemDtoOut));

        mvc.perform(get(REQUEST_MAPPING + "/search")
                        .param("text", "name")
                        .header(X_SHARES_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoOut.getAvailable())));

        verify(itemService, times(1))
                .searchByNameOrDescription(anyInt(), anyInt(), anyString());
    }

    @SneakyThrows
    @Test
    void postComment_whenIsCorrectRequest_thenReturnCommentDtoOut() {
        Long id = 1L;
        when(itemService.createComment(anyLong(), anyLong(), eq(commentDtoIn)))
                .thenReturn(commentDtoOut);

        mvc.perform(post(REQUEST_MAPPING + "/{id}/comment", id)
                        .header(X_SHARES_USER_ID, 1)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoOut.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoOut.getAuthorName())))
                .andExpect(jsonPath("$.created").exists());

        verify(itemService, times(1))
                .createComment(anyLong(), anyLong(), any(CommentDtoIn.class));
    }

}
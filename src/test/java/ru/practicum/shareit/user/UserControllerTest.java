package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.controller.UserControllerImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserControllerImpl.class)
class UserControllerTest {

    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("User")
            .email("user@user.ru")
            .build();

    @SneakyThrows
    @Test
    void post_whenUserDtoIsCorrect_thenReturnUserDtoAndIsOk() {
        when(userService.create(any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).create(any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void post_whenIncorrectEmail_thenReturnBadRequest() {
        userDto.setEmail("email.ru");
        when(userService.create(any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

        verify(userService, never()).create(any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void post_whenBlankName_thenReturnBadRequest() {
        userDto.setName(" ");
        when(userService.create(any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andDo(print());

        verify(userService, never()).create(any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void get_byId() {
        Long id = 1L;
        when(userService.findById(id))
                .thenReturn(userDto);

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andDo(print());

        verify(userService, times(1)).findById(anyLong());
    }

    @SneakyThrows
    @Test
    void getAll() {
        when(userService.findAll())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andDo(print());

        verify(userService, times(1)).findAll();
    }

    @SneakyThrows
    @Test
    void putUser() {
        UserDto updUserDto = UserDto.builder()
                .id(1L)
                .name("updUser")
                .email("upduser@user.ru")
                .build();

        when(userService.update(updUserDto))
                .thenReturn(updUserDto);

        mvc.perform(put("/users")
                        .content(mapper.writeValueAsString(updUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updUserDto.getEmail())));

        verify(userService, times(1)).update(any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void patchUser() {
        Long userId = 1L;
        when(userService.patch(userId, Map.of()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(Map.of()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).patch(anyLong(), anyMap());
    }
}
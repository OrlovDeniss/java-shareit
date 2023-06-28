package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    private static final String PATH = "/users";

    public UserDto get(Long userId) {
        log.info("GET {}/{}", PATH, userId);
        return userService.findById(userId);
    }

    public UserDto post(UserDto userDtoIn) {
        log.info("POST {}, userDtoIn = {}", PATH, userDtoIn);
        return userService.create(userDtoIn);
    }

    public UserDto put(UserDto userDtoIn) {
        log.info("PUT {}, userDtoIn = {}", PATH, userDtoIn);
        return userService.update(userDtoIn);
    }

    public UserDto patch(Long userId, Map<String, Object> newFields) {
        log.info("PATCH {}/{}, newFields = {}", PATH, userId, newFields);
        return userService.patch(userId, newFields);
    }

    public List<UserDto> getAll() {
        log.info("GET {}.", PATH);
        return userService.findAll();
    }

    public void delete(Long userId) {
        log.info("DELETE {}/{}.", PATH, userId);
        userService.delete(userId);
    }

}
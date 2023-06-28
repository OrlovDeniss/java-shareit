package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.client.UserClient;

import java.util.Map;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserControllerImpl implements UserController {

    private final UserClient client;

    private static final String PATH = "/users";

    public ResponseEntity<Object> get(Long userId) {
        log.info("GET {}/{}", PATH, userId);
        return client.get(userId);
    }

    public ResponseEntity<Object> post(UserDto userDtoIn) {
        log.info("POST {}, userDtoIn = {}", PATH, userDtoIn);
        return client.post(userDtoIn);
    }

    public ResponseEntity<Object> put(UserDto userDtoIn) {
        log.info("PUT {}, userDtoIn = {}", PATH, userDtoIn);
        return client.put(userDtoIn);
    }

    public ResponseEntity<Object> patch(Long userId, Map<String, Object> fields) {
        log.info("PATCH {}/{}, userDto = {}", PATH, userId, fields);
        return client.patch(userId, fields);
    }

    public ResponseEntity<Object> getAll() {
        log.info("GET {}.", PATH);
        return client.getAll();
    }

    public void delete(Long userId) {
        log.info("DELETE {}/{}.", PATH, userId);
        client.delete(userId);
    }

}
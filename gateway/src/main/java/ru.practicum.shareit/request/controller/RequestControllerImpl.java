package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.RequestDtoIn;
import ru.practicum.shareit.request.client.RequestClient;

import java.util.Collections;
import java.util.Objects;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestControllerImpl implements RequestController {

    private final RequestClient client;

    private static final String PATH = "/requests";

    @Override
    public ResponseEntity<Object> get(Long requestId, Long userId) {
        log.info("GET {}/{}, userId = {}.", PATH, requestId, userId);
        return client.get(requestId, userId);
    }

    @Override
    public ResponseEntity<Object> post(RequestDtoIn requestDtoIn, Long userId) {
        log.info("POST {}, requestDtoIn = {}, userId = {}.", PATH, requestDtoIn, userId);
        return client.post(requestDtoIn, userId);
    }

    @Override
    public ResponseEntity<Object> getAllCreatedByOtherUsers(Integer from, Integer size, Long userId) {
        log.info("GET {}/all, from = {}, size = {}, userId = {}.", PATH, from, size, userId);
        if (Objects.isNull(from) || Objects.isNull(size)) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        }
        return client.getAllCreatedByOtherUsers(from, size, userId);
    }

    @Override
    public ResponseEntity<Object> getAllCreatedByUser(Long ownerId) {
        log.info("GET {}, ownerId = {}.", PATH, ownerId);
        return client.getAllCreatedByUser(ownerId);
    }

}
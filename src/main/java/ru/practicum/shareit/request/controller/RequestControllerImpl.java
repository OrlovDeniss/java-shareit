package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestControllerImpl implements RequestController {

    private final RequestService requestService;

    private static final String PATH = "/requests";

    @Override
    public RequestDtoOut get(Long requestId, Long userId) {
        log.info("GET {}/{}, userId = {}.", PATH, requestId, userId);
        return requestService.findById(requestId, userId);
    }

    @Override
    public RequestDtoOut post(RequestDtoIn requestDtoIn, Long userId) {
        log.info("POST {}, requestDtoIn = {}, userId = {}.", PATH, requestDtoIn, userId);
        return requestService.create(requestDtoIn, userId);
    }

    @Override
    public List<RequestDtoOut> getAllCreatedByOtherUsers(Integer from, Integer size, Long userId) {
        log.info("GET {}/all, from = {}, size = {}, userId = {}.", PATH, from, size, userId);
        if (Objects.isNull(from) || Objects.isNull(size)) {
            return Collections.emptyList();
        }
        return requestService.findAllByUserId(from, size, userId);
    }

    @Override
    public List<RequestDtoOut> getAllCreatedByUser(Long ownerId) {
        log.info("GET {}/all,ownerId = {}.", PATH, ownerId);
        return requestService.findAllByOwnerId(ownerId);
    }

}
package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;

import java.util.List;

public interface RequestController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("{id}")
    RequestDtoOut get(@PathVariable("id") Long requestId,
                      @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @PostMapping
    RequestDtoOut post(@RequestBody RequestDtoIn requestDtoIn,
                       @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @GetMapping("all")
    List<RequestDtoOut> getAllCreatedByOtherUsers(@RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size,
                                                  @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @GetMapping
    List<RequestDtoOut> getAllCreatedByUser(@RequestHeader(value = X_SHARER_USER_ID) Long ownerId);

}
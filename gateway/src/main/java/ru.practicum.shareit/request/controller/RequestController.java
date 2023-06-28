package ru.practicum.shareit.request.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public interface RequestController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("{id}")
    ResponseEntity<Object> get(@PathVariable("id") @Positive Long requestId,
                               @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PostMapping
    ResponseEntity<Object> post(@Valid @RequestBody RequestDtoIn requestDtoIn,
                                @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @GetMapping("all")
    ResponseEntity<Object> getAllCreatedByOtherUsers(@RequestParam(required = false) @PositiveOrZero Integer from,
                                                     @RequestParam(required = false) @Positive Integer size,
                                                     @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @GetMapping
    ResponseEntity<Object> getAllCreatedByUser(@RequestHeader(value = X_SHARER_USER_ID) @Positive Long ownerId);

}
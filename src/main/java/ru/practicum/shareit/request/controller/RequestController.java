package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

public interface RequestController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("{id}")
    RequestDtoOut get(@PathVariable("id") @Positive Long requestId,
                      @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PostMapping
    RequestDtoOut post(@Valid @RequestBody RequestDtoIn requestDtoIn,
                       @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @GetMapping("all")
    List<RequestDtoOut> getAllCreatedByOtherUsers(@RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size,
                                                  @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @GetMapping
    List<RequestDtoOut> getAllCreatedByUser(@RequestHeader(value = X_SHARER_USER_ID) @Positive Long ownerId);

}
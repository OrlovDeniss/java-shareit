package ru.practicum.shareit.item.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

public interface ItemController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";
    String FROM = "0";
    String SIZE = "10";

    @GetMapping("{id}")
    ResponseEntity<Object> get(@PathVariable("id") @Positive Long itemId,
                               @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PostMapping
    ResponseEntity<Object> post(@Valid @RequestBody ItemDtoIn itemDtoIn,
                                @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PutMapping
    ResponseEntity<Object> put(@Valid @RequestBody ItemDtoIn itemDtoIn,
                               @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PatchMapping("{id}")
    ResponseEntity<Object> patch(@PathVariable("id") @Positive Long itemId,
                                 @RequestBody Map<String, Object> fields,
                                 @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @GetMapping
    ResponseEntity<Object> getUserItems(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = SIZE) @Positive Integer size,
                                        @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @GetMapping("search")
    ResponseEntity<Object> searchByNameOrDescription(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = SIZE) @Positive Integer size,
                                                     @RequestParam String text,
                                                     @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PostMapping("{id}/comment")
    ResponseEntity<Object> postComment(@PathVariable("id") Long itemId,
                                       @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                                       @Valid @RequestBody CommentDtoIn commentDtoIn);

}
package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoIn;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;

public interface ItemController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";
    String FROM = "0";
    String SIZE = "10";

    @GetMapping("{id}")
    ItemDtoOut get(@PathVariable("id") @Positive Long itemId,
                   @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PostMapping
    ItemDtoOut post(@Valid @RequestBody ItemDtoIn itemDtoIn,
                    @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PutMapping
    ItemDtoOut put(@Valid @RequestBody ItemDtoIn itemDtoIn,
                   @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @PatchMapping("{id}")
    ItemDtoOut patch(@PathVariable("id") @Positive Long itemId,
                     @RequestBody Map<String, Object> fields,
                     @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @GetMapping
    List<ItemDtoOut> getAllByUserId(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                    @RequestParam(defaultValue = SIZE) @Positive Integer size,
                                    @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId);

    @GetMapping("search")
    List<ItemDtoOut> searchByNameOrDescription(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = SIZE) @Positive Integer size,
                                               @RequestParam String text);

    @PostMapping("{id}/comment")
    CommentDtoOut postComment(@PathVariable("id") Long itemId,
                              @RequestHeader(value = X_SHARER_USER_ID) @Positive Long userId,
                              @Valid @RequestBody CommentDtoIn commentDtoIn);

}
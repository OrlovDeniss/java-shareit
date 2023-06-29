package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;

import java.util.List;
import java.util.Map;

public interface ItemController {

    String X_SHARER_USER_ID = "X-Sharer-User-Id";
    String FROM = "0";
    String SIZE = "10";

    @GetMapping("{id}")
    ItemDtoOut get(@PathVariable("id") Long itemId,
                   @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @PostMapping
    ItemDtoOut post(@RequestBody ItemDtoIn itemDtoIn,
                    @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @PutMapping
    ItemDtoOut put(@RequestBody ItemDtoIn itemDtoIn,
                   @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @PatchMapping("{id}")
    ItemDtoOut patch(@PathVariable("id") Long itemId,
                     @RequestBody Map<String, Object> fields,
                     @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @GetMapping
    List<ItemDtoOut> getAllByUserId(@RequestParam(defaultValue = FROM) Integer from,
                                    @RequestParam(defaultValue = SIZE) Integer size,
                                    @RequestHeader(value = X_SHARER_USER_ID) Long userId);

    @GetMapping("search")
    List<ItemDtoOut> searchByNameOrDescription(@RequestParam(defaultValue = FROM) Integer from,
                                               @RequestParam(defaultValue = SIZE) Integer size,
                                               @RequestParam String text);

    @PostMapping("{id}/comment")
    CommentDtoOut postComment(@PathVariable("id") Long itemId,
                              @RequestHeader(value = X_SHARER_USER_ID) Long userId,
                              @RequestBody CommentDtoIn commentDtoIn);

}
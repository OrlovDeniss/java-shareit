package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.client.ItemClient;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemControllerImpl implements ItemController {

    private final ItemClient client;

    private static final String PATH = "/items";

    @Override
    public ResponseEntity<Object> get(Long itemId, Long userId) {
        log.info("GET {}/{}, userId = {}.", PATH, itemId, userId);
        return client.get(itemId, userId);
    }

    @Override
    public ResponseEntity<Object> post(ItemDtoIn itemDtoIn, Long userId) {
        log.info("POST {}, itemDtoIn = {}, userId = {}.", PATH, itemDtoIn, userId);
        return client.post(itemDtoIn, userId);
    }

    @Override
    public ResponseEntity<Object> put(ItemDtoIn itemDtoIn, Long userId) {
        log.info("PUT {}, itemDtoIn = {}, userId = {}.", PATH, itemDtoIn, userId);
        return client.put(itemDtoIn, userId);
    }

    @Override
    public ResponseEntity<Object> patch(Long itemId, Map<String, Object> fields, Long userId) {
        log.info("PATCH {}, itemId = {}, fields = {}, userId = {}.", PATH, itemId, fields, userId);
        return client.patch(itemId, fields, userId);
    }

    @Override
    public ResponseEntity<Object> getUserItems(Integer from, Integer size, Long userId) {
        log.info("GET {}, from = {}, size = {}, userId = {}.", PATH, from, size, userId);
        return client.getUserItems(from, size, userId);
    }

    @Override
    public ResponseEntity<Object> searchByNameOrDescription(Integer from, Integer size, String text, Long userId) {
        log.info("GET {}/search, from = {}, size = {}, text = {}.", PATH, from, size, text);
        if (text.isBlank()) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        }
        return client.searchByNameOrDescription(from, size, text, userId);
    }

    @Override
    public ResponseEntity<Object> postComment(Long itemId, Long userId, CommentDtoIn commentDtoIn) {
        log.info("POST {}/{}/comment, userId = {}, commentDtoIn = {}.", PATH, itemId, userId, commentDtoIn);
        return client.createComment(itemId, userId, commentDtoIn);
    }

}
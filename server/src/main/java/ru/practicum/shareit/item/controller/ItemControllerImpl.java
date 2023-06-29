package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemControllerImpl implements ItemController {

    private final ItemService itemService;

    private static final String PATH = "/items";

    @Override
    public ItemDtoOut get(Long itemId, Long userId) {
        log.info("GET {}/{}, userId = {}.", PATH, itemId, userId);
        return itemService.findById(itemId, userId);
    }

    @Override
    public ItemDtoOut post(ItemDtoIn itemDtoIn, Long userId) {
        log.info("POST {}, itemDtoIn = {}, userId = {}.", PATH, itemDtoIn, userId);
        return itemService.create(itemDtoIn, userId);
    }

    @Override
    public ItemDtoOut put(ItemDtoIn itemDtoIn, Long userId) {
        log.info("PUT {}, itemDtoIn = {}, userId = {}.", PATH, itemDtoIn, userId);
        return itemService.update(itemDtoIn, userId);
    }

    @Override
    public ItemDtoOut patch(Long itemId, Map<String, Object> fields, Long userId) {
        log.info("PATCH {}, itemId = {}, fields = {}, userId = {}.", PATH, itemId, fields, userId);
        return itemService.patch(itemId, fields, userId);
    }

    @Override
    public List<ItemDtoOut> getAllByUserId(Integer from, Integer size, Long userId) {
        log.info("GET {}, from = {}, size = {}, userId = {}.", PATH, from, size, userId);
        return itemService.findAllByUserId(from, size, userId);
    }

    @Override
    public List<ItemDtoOut> searchByNameOrDescription(Integer from, Integer size, String text) {
        log.info("GET {}/search, from = {}, size = {}, text = {}.", PATH, from, size, text);
        return itemService.searchByNameOrDescription(from, size, text);
    }

    @Override
    public CommentDtoOut postComment(Long itemId, Long userId, CommentDtoIn commentDtoIn) {
        log.info("POST {}/{}/comment, userId = {}, commentDtoIn = {}.", PATH, itemId, userId, commentDtoIn);
        return itemService.createComment(itemId, userId, commentDtoIn);
    }

}
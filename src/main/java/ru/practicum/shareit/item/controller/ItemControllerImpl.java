package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.abstraction.controller.AbstractController;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoIn;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemControllerImpl extends AbstractController implements ItemController {

    private final ItemService itemService;

    @Override
    public ItemDtoOut get(Long itemId, Long userId) {
        return itemService.findById(itemId, userId);
    }

    @Override
    public ItemDtoOut post(ItemDtoIn itemDtoIn, Long userId) {
        return itemService.create(itemDtoIn, userId);
    }

    @Override
    public ItemDtoOut put(ItemDtoIn itemDtoIn, Long userId) {
        return itemService.update(itemDtoIn, userId);
    }

    @Override
    public ItemDtoOut patch(Long itemId, Map<String, Object> fields, Long userId) {
        return itemService.patch(itemId, fields, userId);
    }

    @Override
    public List<ItemDtoOut> getAllByUserId(Integer from, Integer size, Long userId) {
        throwWhenFromLessThenZero(from);
        throwWhenSizeLessThanOne(size);
        return itemService.findAllByUserId(from, size, userId);
    }

    @Override
    public List<ItemDtoOut> searchByNameOrDescription(Integer from, Integer size, String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchByNameOrDescription(from, size, text);
    }

    @Override
    public CommentDtoOut postComment(Long itemId, Long userId, CommentDtoIn commentDtoIn) {
        return itemService.createComment(itemId, userId, commentDtoIn);
    }

}
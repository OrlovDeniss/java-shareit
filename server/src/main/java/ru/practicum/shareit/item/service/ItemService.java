package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;

import java.util.List;
import java.util.Map;

public interface ItemService {

    ItemDtoOut create(ItemDtoIn itemDtoIn, Long userId);

    ItemDtoOut update(ItemDtoIn itemDtoIn, Long userId);

    ItemDtoOut patch(Long itemId, Map<String, Object> fields, Long userId);

    ItemDtoOut findById(Long itemId, Long userId);

    List<ItemDtoOut> searchByNameOrDescription(Integer from, Integer size, String text);

    CommentDtoOut createComment(Long itemId, Long userId, CommentDtoIn commentDtoIn);

    List<ItemDtoOut> findAllByUserId(Integer from, Integer size, Long userId);

}
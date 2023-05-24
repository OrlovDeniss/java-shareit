package ru.practicum.shareit.item.service;

import ru.practicum.shareit.abstraction.userobject.service.UserObjectService;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoIn;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;

import java.util.List;

public interface ItemService extends UserObjectService<ItemDtoIn, ItemDtoOut> {

    ItemDtoOut findById(Long itemId, Long userId);

    List<ItemDtoOut> search(String text);

    CommentDtoOut createComment(Long itemId, Long userId, CommentDtoIn commentDtoIn);

}
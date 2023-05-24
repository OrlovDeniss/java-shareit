package ru.practicum.shareit.item.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.abstraction.userobject.controller.AbstractUserObjectController;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoIn;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.exception.general.MethodNotAllowedException;

import java.util.Collections;
import java.util.List;

@RestController
@Validated
@RequestMapping("/items")
public class ItemControllerImpl extends AbstractUserObjectController<ItemDtoIn, ItemDtoOut> implements ItemController {

    private final ItemService itemService;

    public ItemControllerImpl(ItemService itemService) {
        super(itemService);
        this.itemService = itemService;
    }

    @Override
    public ItemDtoOut get(Long itemId, Long userId) {
        throwWhenUserNotFound(userId);
        return itemService.findById(itemId, userId);
    }

    @Override
    public List<ItemDtoOut> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.search(text);
    }

    @Override
    public void delete(Long objectId, Long userId) {
        throw new MethodNotAllowedException();
    }

    @Override
    public CommentDtoOut addComment(Long itemId, Long userId, CommentDtoIn commentDtoIn) {
        return itemService.createComment(itemId, userId, commentDtoIn);
    }

}
package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.abstraction.userobject.controller.UserObjectController;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

public interface ItemController extends UserObjectController<ItemDtoIn, ItemDtoOut> {

    @GetMapping("search")
    List<ItemDtoOut> search(@RequestParam String text);

    @PostMapping("{id}/comment")
    CommentDtoOut addComment(@PathVariable("id") Long itemId,
                             @RequestHeader(value = USER_ID) @Positive Long userId,
                             @Valid @RequestBody CommentDtoIn commentDtoIn);

}
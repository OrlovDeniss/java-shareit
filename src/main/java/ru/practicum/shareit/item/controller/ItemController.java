package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.abstraction.userobject.controller.UserObjectController;

import java.util.List;

public interface ItemController extends UserObjectController<ItemDto> {

    @GetMapping("search")
    List<ItemDto> search(@RequestParam String text);

}
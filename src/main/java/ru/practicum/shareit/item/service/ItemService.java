package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.abstraction.userobject.service.UserObjectService;

import java.util.List;

public interface ItemService extends UserObjectService<ItemDto> {

    List<ItemDto> search(String text);

}
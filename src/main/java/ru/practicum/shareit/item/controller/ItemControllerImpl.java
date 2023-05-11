package ru.practicum.shareit.item.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.abstraction.userobject.controller.AbstractUserObjectController;

import java.util.Collections;
import java.util.List;

@RestController
@Validated
@RequestMapping("/items")
public class ItemControllerImpl extends AbstractUserObjectController<ItemDto> implements ItemController {

    public ItemControllerImpl(ItemService service) {
        super(service);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return getService().search(text);
    }

    @Override
    public ItemService getService() {
        return (ItemService) getSuperService();
    }
}
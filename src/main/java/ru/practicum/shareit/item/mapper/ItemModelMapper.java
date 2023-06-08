package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.AbstractModelMapper;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemModelMapper extends AbstractModelMapper<ItemDtoIn, ItemDtoOut, Item> {

    @Override
    public ItemDtoOut toDto(Item item) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(Objects.isNull(item.getRequest()) ? null : item.getRequest().getId())
                .build();
    }

    @Override
    public Item toEntity(ItemDtoIn itemDtoIn) {
        return Item.builder()
                .id(itemDtoIn.getId())
                .name(itemDtoIn.getName())
                .description(itemDtoIn.getDescription())
                .available(itemDtoIn.getAvailable())
                .build();
    }

    public ItemDtoShort toDtoShort(Item item) {
        return ItemDtoShort.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(Objects.isNull(item.getRequest()) ? null : item.getRequest().getId())
                .ownerId(item.getUser().getId())
                .build();
    }

    public List<ItemDtoShort> toDtoShort(List<Item> items) {
        return Objects.isNull(items) ?
                Collections.emptyList() : items.stream().map(this::toDtoShort).collect(Collectors.toList());
    }

}
package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(uses = CommentMapper.class)
public interface ItemMapper extends ModelMapper<ItemDtoIn, ItemDtoOut, Item> {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Override
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDtoOut toDto(Item item);

    @Mapping(target = "requestId", source = "item.request.id")
    @Mapping(target = "ownerId", source = "item.user.id")
    ItemDtoShort toDtoShort(Item item);

    List<ItemDtoShort> toDtoShort(List<Item> items);

}
package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.AbstractModelMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class ItemRequestModelMapper extends AbstractModelMapper<ItemRequestDto, ItemRequest> {

    @Override
    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getUser())
                .created(itemRequest.getCreated())
                .build();
    }

    @Override
    public ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .user(itemRequestDto.getRequestor())
                .created(itemRequestDto.getCreated())
                .build();
    }
}
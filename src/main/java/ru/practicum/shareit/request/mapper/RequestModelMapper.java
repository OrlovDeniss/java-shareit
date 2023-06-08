package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.abstraction.mapper.AbstractModelMapper;
import ru.practicum.shareit.item.mapper.ItemModelMapper;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;

@Component
@RequiredArgsConstructor
public class RequestModelMapper extends AbstractModelMapper<RequestDtoIn, RequestDtoOut, Request> {

    private final ItemModelMapper itemModelMapper;

    @Override
    public RequestDtoOut toDto(Request request) {
        return RequestDtoOut.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemModelMapper.toDtoShort(request.getItems()))
                .build();
    }

    @Override
    public Request toEntity(RequestDtoIn requestDtoIn) {
        return Request.builder()
                .id(requestDtoIn.getId())
                .description(requestDtoIn.getDescription())
                .build();
    }

}
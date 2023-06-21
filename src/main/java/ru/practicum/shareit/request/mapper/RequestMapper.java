package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;

@Mapper(uses = ItemMapper.class)
public interface RequestMapper extends ModelMapper<RequestDtoIn, RequestDtoOut, Request> {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

}
package ru.practicum.shareit.request.service;

import ru.practicum.shareit.abstraction.userobject.UserObjectService;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;

import java.util.List;

public interface RequestService extends UserObjectService<RequestDtoIn, RequestDtoOut> {

    List<RequestDtoOut> findAllByUserId(Integer from, Integer size, Long userId);

}
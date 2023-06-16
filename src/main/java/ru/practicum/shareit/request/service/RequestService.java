package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;

import java.util.List;

public interface RequestService {

    RequestDtoOut create(RequestDtoIn requestDtoIn, Long userId);

    RequestDtoOut findById(Long requestId, Long userId);

    List<RequestDtoOut> findAllByUserId(Integer from, Integer size, Long userId);

    List<RequestDtoOut> findAllByOwnerId(Long ownerId);

}
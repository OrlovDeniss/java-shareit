package ru.practicum.shareit.abstraction.service;

public interface Service<I, O> {

    O create(I inputDto, Long userId);

    O findById(Long objectId, Long userId);

}
package ru.practicum.shareit.abstraction.userobject.controller;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.abstraction.userobject.service.UserObjectService;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractUserObjectController<I extends Identified, O>
        implements UserObjectController<I, O> {

    private final UserObjectService<I, O> service;

    public O get(Long objectId, Long userId) {
        throwWhenUserNotFound(userId);
        return service.findById(objectId);
    }

    public O add(I dtoIn, Long userId) {
        throwWhenUserNotFound(userId);
        return service.create(dtoIn, userId);
    }

    public O update(I dtoIn, Long userId) {
        throwWhenUserNotFound(userId);
        return service.update(dtoIn, userId);
    }

    public O patch(Long objectId, Map<String, Object> fields, Long userId) {
        throwWhenUserNotOwnObject(objectId, userId);
        return service.patch(objectId, fields);
    }

    public List<O> findAllByUserId(Long userId) {
        throwWhenUserNotFound(userId);
        return service.findAllByUserId(userId);
    }

    public void delete(Long objectId, Long userId) {
        throwWhenUserNotOwnObject(objectId, userId);
        service.delete(objectId, userId);
    }

    protected void throwWhenUserNotFound(Long userId) {
        if (!service.userExistsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь id = %d не найден!", userId));
        }
    }

    protected void throwWhenUserNotOwnObject(Long objectId, Long userId) {
        service.throwWhenUserNotOwnObject(objectId, userId);
    }

}
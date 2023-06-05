package ru.practicum.shareit.abstraction.userobject.service;

import ru.practicum.shareit.abstraction.model.Identified;

import java.util.List;
import java.util.Map;

public interface UserObjectService<I extends Identified, O> {

    O findById(Long objectId);

    List<O> findAllByUserId(Long userId);

    O create(I in, Long userId);

    O update(I in, Long userId);

    O patch(Long id, Map<String, Object> fields, Long userId);

    void delete(Long id, Long userId);

    boolean userExistsById(Long userId);

    void throwWhenUserNotOwnObject(Long objectId, Long userId);

}
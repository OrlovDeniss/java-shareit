package ru.practicum.shareit.abstraction.userobject;

import ru.practicum.shareit.abstraction.model.Identified;

import java.util.List;
import java.util.Map;

public interface UserObjectService<I extends Identified, O> {

    O findById(Long objectId);

    O findById(Long objectId, Long userId);

    List<O> findAllByOwnerId(Long userId);

    O create(I in, Long userId);

    O update(I in, Long userId);

    O patch(Long id, Map<String, Object> fields, Long userId);

}
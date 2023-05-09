package ru.practicum.shareit.abstraction.userobject.repository;

import ru.practicum.shareit.abstraction.model.Entity;
import ru.practicum.shareit.abstraction.repository.Repository;

import java.util.List;

public interface UserObjectRepository<E extends Entity> extends Repository<E> {

    List<E> findAllByUserId(Long userId);

    void deleteUserObject(Long objectId, Long userId);

    List<E> findALlWhereIdIn(List<Long> objectsId);

}
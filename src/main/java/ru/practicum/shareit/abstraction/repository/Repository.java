package ru.practicum.shareit.abstraction.repository;

import ru.practicum.shareit.abstraction.model.Entity;

import java.util.List;
import java.util.Optional;

public interface Repository<E extends Entity> {

    E save(E e);

    E update(E e);

    Optional<E> findById(Long id);

    void delete(Long id);

    boolean existsById(Long id);

    List<E> findAllWhereIdIn(List<Long> id);

}
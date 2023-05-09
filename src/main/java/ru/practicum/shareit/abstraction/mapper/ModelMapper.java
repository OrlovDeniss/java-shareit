package ru.practicum.shareit.abstraction.mapper;

import ru.practicum.shareit.abstraction.model.Entity;

import java.util.List;

public interface ModelMapper<D, E extends Entity> {

    D toDto(E e);

    List<D> toDto(List<E> e);

    E toEntity(D d);

}
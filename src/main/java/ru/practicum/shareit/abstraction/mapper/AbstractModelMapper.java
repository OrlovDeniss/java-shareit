package ru.practicum.shareit.abstraction.mapper;

import ru.practicum.shareit.abstraction.model.Entity;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractModelMapper<D, E extends Entity> implements ModelMapper<D, E> {

    @Override
    public List<D> toDto(List<E> e) {
        return e.stream().map(this::toDto).collect(Collectors.toList());
    }

}
package ru.practicum.shareit.abstraction.mapper;

import java.util.List;

public interface ModelMapper<I, O, E> {

    O toDto(E entity);

    E toEntity(I dtoIn);

    List<O> toDto(List<E> entities);

}
package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.util.exception.general.JsonUpdateFieldsException;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractService<I, O, E extends Identified> {

    private final ModelMapper<I, O, E> mapper;
    protected final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    protected E tryUpdateFields(E entity, Map<String, Object> newFields) {
        try {
            return objectMapper.updateValue(entity, newFields);
        } catch (JsonMappingException e) {
            throw new JsonUpdateFieldsException(
                    String.format("Невозможно обновить поля объекта: %s", entity.getClass().getSimpleName()));
        }
    }

    public E toEntity(I in) {
        return mapper.toEntity(in);
    }

    public O toDto(E e) {
        return mapper.toDto(e);
    }

    public List<O> toDto(List<E> listIn) {
        return mapper.toDto(listIn);
    }

}
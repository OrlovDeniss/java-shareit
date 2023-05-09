package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.abstraction.model.Entity;
import ru.practicum.shareit.exception.JsonUpdateFieldsException;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractService<D, E extends Entity> {

    protected final ModelMapper<D, E> mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected D tryUpdateFields(D dto, Map<String, Object> newFields) {
        try {
            return objectMapper.updateValue(dto, newFields);
        } catch (JsonMappingException e) {
            throw new JsonUpdateFieldsException(
                    String.format("Невозможно обновить поля объекта: %s", dto.getClass().getSimpleName()));
        }
    }

}
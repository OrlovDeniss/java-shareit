package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.util.exception.general.JsonUpdateFieldsException;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractService<I, O, E> implements Service<I, O> {

    protected final JpaRepository<E, Long> repository;
    private final ModelMapper<I, O, E> mapper;
    private final ObjectMapper objectMapper;

    protected E tryUpdateFields(E entity, Map<String, Object> newFields) {
        try {
            return objectMapper.updateValue(entity, newFields);
        } catch (JsonMappingException e) {
            throw new JsonUpdateFieldsException(
                    String.format("Невозможно обновить поля объекта: %s", e.getClass().getSimpleName()));
        }
    }

    protected E toEntity(I inputDto) {
        return mapper.toEntity(inputDto);
    }

    protected O toDto(E entity) {
        return mapper.toDto(entity);
    }

    protected List<O> toDto(List<E> inputDtoList) {
        return mapper.toDto(inputDtoList);
    }

}
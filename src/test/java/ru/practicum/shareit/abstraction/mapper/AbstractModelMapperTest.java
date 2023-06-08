package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
abstract class AbstractModelMapperTest<I, O, E> {

    @Autowired
    ModelMapper<I, O, E> mapper;

    protected abstract E getEntity();

    protected abstract I getDtoIn();

    protected abstract O getDtoOut();

    @Test
    void toDtoTest() {
        assertEquals(getDtoOut(), mapper.toDto(getEntity()));
    }

    @Test
    void toDtoListTest() {
        assertArrayEquals(List.of(getDtoOut()).toArray(), mapper.toDto(List.of(getEntity())).toArray());
    }

    @Test
    void toEntityTest() {
        assertEquals(getEntity(), mapper.toEntity(getDtoIn()));
    }
}
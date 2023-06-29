package ru.practicum.shareit.abstraction.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractModelMapperTest<I, O, E> {

    protected final ModelMapper<I, O, E> mapper;
    protected final EasyRandom generator = new EasyRandom();

    protected AbstractModelMapperTest(ModelMapper<I, O, E> mapper) {
        this.mapper = mapper;
    }

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
package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.util.exception.general.JsonUpdateFieldsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractServiceTest<E extends Identified> {

    static AbstractService service = Mockito.mock(AbstractService.class, Mockito.CALLS_REAL_METHODS);

    protected abstract E getEntity();

    protected abstract Map<String, Object> getNewFields();

    protected abstract E getReference();

    @BeforeAll
    static void setUp() {
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());
    }

    @Test
    void tryUpdateFields_whenCorrectMap_thenReturnUpdatedEntity() {
        assertEquals(service.tryUpdateFields(getEntity(), getNewFields()), getReference());
    }

    @Test
    void tryUpdateFields_whenWrongMap_thenThrowJsonUpdateFieldsException() {
        assertThrows(JsonUpdateFieldsException.class,
                () -> service.tryUpdateFields(getEntity(), Map.of("aurnfj7mn3", "prhjfns8j3")));
    }

}
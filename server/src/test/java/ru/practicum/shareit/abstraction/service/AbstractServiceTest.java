package ru.practicum.shareit.abstraction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.util.config.ObjectMapperConfig;
import ru.practicum.shareit.util.exception.general.JsonUpdateFieldsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractServiceTest<E> {

    private final ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper();
    protected final EasyRandom generator = new EasyRandom();
    protected AbstractService service;


    protected abstract E getEntity();

    protected abstract Map<String, Object> getNewFields();

    protected abstract E getReference();

    @BeforeEach
    void setUp() {
        service = Mockito.mock(AbstractService.class, Mockito.CALLS_REAL_METHODS);;
        ReflectionTestUtils.setField(service, "objectMapper", objectMapper);
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
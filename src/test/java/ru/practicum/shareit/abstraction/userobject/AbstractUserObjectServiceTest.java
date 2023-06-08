package ru.practicum.shareit.abstraction.userobject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.abstraction.mapper.AbstractModelMapper;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.exception.user.UserOwnsObjectException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

abstract class AbstractUserObjectServiceTest<I extends Identified, O, E extends UserObject> {

    static UserRepository userRepository = Mockito.mock(UserRepository.class);

    static UserObjectRepository objectRepository = Mockito.mock(UserObjectRepository.class);

    static AbstractUserObjectService service = Mockito.mock(AbstractUserObjectService.class, Mockito.CALLS_REAL_METHODS);

    static AbstractModelMapper mapper = Mockito.mock(AbstractModelMapper.class, Mockito.CALLS_REAL_METHODS);

    final User user = new User(1L, "name", "email@email.com");

    protected abstract E getEntity();

    protected abstract I getDtoIn();

    protected abstract O getDtoOut();

    @BeforeAll
    static void setUp() {
        ReflectionTestUtils.setField(service, "userRepository", userRepository);
        ReflectionTestUtils.setField(service, "objectRepository", objectRepository);
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(service, "mapper", mapper);
    }

    @BeforeEach
    void beforeEach() {
        when(mapper.toEntity(any(getDtoIn().getClass())))
                .thenReturn(getEntity());
        when(mapper.toDto(any(getEntity().getClass())))
                .thenReturn(getDtoOut());
    }

    @Test
    void findById() {
        when(objectRepository.findById(anyLong()))
                .thenReturn(Optional.of(getEntity()));

        assertEquals(service.findById(getEntity().getId()), getDtoOut());
    }

    @Test
    void testFindById() {
        Long userId = 1L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(objectRepository.findById(anyLong()))
                .thenReturn(Optional.of(getEntity()));

        assertEquals(service.findById(getEntity().getId(), userId), getDtoOut());
    }

    @Test
    void testFindById_whenUserNotFound_thenThrowUserNotFoundException() {
        Long userId = 1L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.findById(getEntity().getId(), userId));
    }

    @Test
    void findAllByOwnerId_whenUserFound_thenReturnObject() {
        Long userId = 1L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(objectRepository.findAllByUserId(anyLong()))
                .thenReturn(List.of(getEntity()));

        assertArrayEquals(service.findAllByOwnerId(userId).toArray(), List.of(getDtoOut()).toArray());
    }

    @Test
    void findAllByOwnerId_whenUserNotFound_thenThrowUserNotFoundException() {
        Long userId = 1L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.findAllByOwnerId(userId));
    }

    @Test
    void create_whenUserFound_thenReturnObject() {
        Long userId = 3L;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(objectRepository.save(any()))
                .then(returnsFirstArg());

        assertNotNull(service.create(getDtoIn(), userId));
    }

    @Test
    void create_whenUserNotFound_thenThrowUserNotFoundException() {
        Long userId = 3L;

        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.findAllByOwnerId(userId));
    }

    @Test
    void update_whenUserNotFound_thenUpdateAndReturnObject() {
        Long userId = 3L;

        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.update(getDtoIn(), userId));
    }

    @Test
    void update_whenUserFound_thenUpdateAndReturnObject() {
        Long userId = 3L;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(objectRepository.save(any()))
                .then(returnsFirstArg());

        assertNotNull(service.update(getDtoIn(), userId));
    }

    @Test
    void patch_whenUserNotFound_thenReturnObject() {
        Long userId = 77L;
        E e = getEntity();
        e.setUser(user);

        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.patch(e.getId(), Map.of(), userId));
    }

    @Test
    void patch_whenUserFound_thenReturnObject() {
        Long userId = user.getId();
        E e = getEntity();
        e.setUser(user);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(objectRepository.findById(anyLong()))
                .thenReturn(Optional.of(e));
        when(objectRepository.save(any()))
                .then(returnsFirstArg());

        assertNotNull(service.patch(getDtoIn().getId(), Map.of(), userId));
    }

    @Test
    void patch_whenUserNotOwner_thenThrowUserOwnsObjectException() {
        Long otherUserId = 5L;
        E e = getEntity();
        e.setUser(user);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(objectRepository.findById(anyLong()))
                .thenReturn(Optional.of(e));
        when(objectRepository.save(any()))
                .then(returnsFirstArg());

        assertThrows(UserOwnsObjectException.class,
                () -> service.patch(getDtoIn().getId(), Map.of(), otherUserId));
    }
}
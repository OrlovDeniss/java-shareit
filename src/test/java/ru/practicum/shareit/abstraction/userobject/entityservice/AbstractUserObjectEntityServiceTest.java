package ru.practicum.shareit.abstraction.userobject.entityservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.abstraction.userobject.AbstractUserObjectEntityService;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.general.EntityNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.exception.user.UserOwnsObjectException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

abstract class AbstractUserObjectEntityServiceTest<E extends UserObject> {

    static UserRepository userRepository = Mockito.mock(UserRepository.class);

    static UserObjectRepository objectRepository = Mockito.mock(UserObjectRepository.class);

    static AbstractUserObjectEntityService service = Mockito.mock(AbstractUserObjectEntityService.class, Mockito.CALLS_REAL_METHODS);

    final User user = new User(1L, "name", "email@email.com");

    protected abstract E getEntity();

    @BeforeAll
    static void setUp() {
        ReflectionTestUtils.setField(service, "userRepository", userRepository);
        ReflectionTestUtils.setField(service, "objectRepository", objectRepository);
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());
    }

    @Test
    void findUserObjectById_whenObjectFound_thenReturnObject() {
        when(objectRepository.findById(anyLong()))
                .thenReturn(Optional.of(getEntity()));

        assertEquals(service.findUserObjectById(getEntity().getId()), getEntity());
    }

    @Test
    void findUserObjectById_whenObjectNotFound_thenThrow() {
        when(objectRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.findUserObjectById(getEntity().getId()));
    }

    @Test
    void findAllUserObjectsByUserId_whenUserFound_thenReturnListOfObjects() {
        Long userId = 1L;

        when(objectRepository.findAllByUserId(anyLong()))
                .thenReturn(List.of(getEntity()));

        assertArrayEquals(service.findAllUserObjectsByUserId(userId).toArray(), List.of(getEntity()).toArray());
    }

    @Test
    void findAllUserObjectsByUserId_whenUserFound_thenReturnEmptyList() {
        Long userId = 1L;

        when(objectRepository.findAllByUserId(anyLong()))
                .thenReturn(List.of());

        assertThat(service.findAllUserObjectsByUserId(userId))
                .isEmpty();
    }

    @Test
    void createUserObject_whenUserFound_thenReturnObject() {
        Long userId = 3L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(objectRepository.save(any()))
                .then(returnsFirstArg());

        assertEquals(service.createUserObject(getEntity(), userId).getUser(), user);
    }

    @Test
    void createUserObject_whenUserNotFound_thenThrowUserNotFound() {
        Long userId = 3L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(objectRepository.save(any()))
                .then(returnsFirstArg());

        assertThrows(UserNotFoundException.class,
                () -> service.createUserObject(getEntity(), userId));
    }

    @Test
    void patchUserObject_whenObjectFound_thenReturnUpdatedObject() {
        when(objectRepository.findById(anyLong()))
                .thenReturn(Optional.of(getEntity()));
        when(objectRepository.save(any()))
                .then(returnsFirstArg());

        assertEquals(service.patchUserObject(getEntity().getId(), Map.of()), getEntity());
    }

    @Test
    void patchUserObject_whenObjectNotFound_thenThrowEntityNotFoundException() {
        when(objectRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(objectRepository.save(any()))
                .then(returnsFirstArg());

        assertThrows(EntityNotFoundException.class,
                () -> service.patchUserObject(getEntity().getId(), Map.of()));
    }

    @Test
    void userExistsById_whenUserNotExists_returnFalse() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertFalse(service.userExistsById(1L));
    }

    @Test
    void userExistsById_whenExists_returnTrue() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        assertTrue(service.userExistsById(1L));
    }

    @Test
    void throwWhenUserNotOwnObject() {
        E e = getEntity();
        e.setUser(user);

        when(objectRepository.findById(anyLong()))
                .thenReturn(Optional.of(e));

        assertThrows(UserOwnsObjectException.class,
                () -> service.throwWhenUserNotOwnObject(e.getId(), 5L));
    }

}
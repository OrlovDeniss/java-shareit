package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.config.ObjectMapperConfig;
import ru.practicum.shareit.util.exception.user.UserEmailAlreadyExistsException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserServiceImpl userService;
    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserServiceImpl.class, CALLS_REAL_METHODS);
    }

    @Test
    void create() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        UserDto userDtoIn = generator.nextObject(UserDto.class);
        when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());
        assertEquals(userDtoIn, userService.create(userDtoIn));
        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void update_whenUserNotExist_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        UserDto userDtoIn = generator.nextObject(UserDto.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> userService.update(userDtoIn));
        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void update_whenUserExist_thenReturnUserDto() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        UserDto userDtoIn = generator.nextObject(UserDto.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());
        assertEquals(userDtoIn, userService.update(userDtoIn));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void patch_whenEmailAlreadyExists_thanThrowUserEmailAlreadyExistsException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong()))
                .thenReturn(true);
        assertThrows(UserEmailAlreadyExistsException.class,
                () -> userService.patch(generator.nextLong(), Map.of("email", "user@user.com")));
        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void patch_whenUserNotExist_thanThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> userService.patch(generator.nextLong(), Map.of("email", "user@user.com")));
        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void patch_whenCorrect_thanReturnUserDto() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "objectMapper", new ObjectMapperConfig().objectMapper());
        User user = generator.nextObject(User.class);
        String newEmail = "user@user.com";
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());
        UserDto patched = userService.patch(generator.nextLong(), Map.of("email", newEmail));
        assertEquals(user.getId(), patched.getId());
        assertEquals(user.getName(), patched.getName());
        assertEquals(newEmail, patched.getEmail());
        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void findById_whenUserNotExist_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> userService.findById(generator.nextLong()));
        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void findById_whenCorrect_thenReturnUserDto() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        User user = generator.nextObject(User.class);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserDto found = userService.findById(generator.nextLong());
        assertEquals(user.getId(), found.getId());
        assertEquals(user.getName(), found.getName());
        assertEquals(user.getEmail(), found.getEmail());
        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void findAll() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        User user = generator.nextObject(User.class);
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        List<UserDto> foundList = userService.findAll();
        assertThat(foundList).hasSize(1);
        UserDto found = foundList.get(0);
        assertEquals(user.getId(), found.getId());
        assertEquals(user.getName(), found.getName());
        assertEquals(user.getEmail(), found.getEmail());
        verify(userRepository, times(1))
                .findAll();
    }

    @Test
    void delete_whenUserNotExist_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> userService.delete(generator.nextLong()));
        verify(userRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void delete_whenUserFound_thenDeleteUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        userService.delete(generator.nextLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

}
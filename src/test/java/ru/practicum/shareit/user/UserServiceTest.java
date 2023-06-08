package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.exception.user.UserEmailAlreadyExistsException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserServiceImpl userService;
    @MockBean
    UserRepository userRepository;

    final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@user.com")
            .build();

    final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@user.com")
            .build();

    final UserDto user1Dto = UserDto.builder()
            .id(1L)
            .name("user1")
            .email("user1@user.com")
            .build();

    @Test
    void findById_whenUserNotFound_thenThrowUserNotFoundException() {
        Long userId = 1L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.findById(userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findById_whenUserFound_thenReturnUserDto() {
        Long userId = user1.getId();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));

        assertEquals(userService.findById(userId), user1Dto);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void create_whenIsCorrectUserDto_thenSaveAndReturnUserDto() {
        when(userRepository.save(user1))
                .then(returnsFirstArg());

        assertEquals(userService.create(user1Dto), user1Dto);

        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void update_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.update(user1Dto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_whenUserFound_thenReturnUserDto() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.save(user1))
                .then(returnsFirstArg());

        assertEquals(userService.update(user1Dto), user1Dto);

        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void patch_whenEmailAlreadyExists_thanThrowUserEmailAlreadyExistsException() {
        Long userId = user1.getId();

        when(userRepository.existsByEmail(user2.getEmail()))
                .thenReturn(true);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());

        assertThrows(UserEmailAlreadyExistsException.class,
                () -> userService.patch(userId, Map.of("email", "user2@user.com")));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void patch_whenUserNotFound_thenThrowUserNotFound() {
        Long userId = user1.getId();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.patch(userId, Map.of("email", "user2@user.com")));

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void patch_whenCorrectParams_thenReturnUserDto() {
        Long userId = user1.getId();

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());

        assertEquals(userService.patch(userId, Map.of()), user1Dto);

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void findAll() {
        when(userRepository.findAll())
                .thenReturn(List.of(user1));

        assertArrayEquals(List.of(user1Dto).toArray(), userService.findAll().toArray());

        verify(userRepository, times(1))
                .findAll();
    }

    @Test
    void delete_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.delete(1L));

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_whenUserFound_thenDeleteUser() {
        Long userId = 1L;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

}
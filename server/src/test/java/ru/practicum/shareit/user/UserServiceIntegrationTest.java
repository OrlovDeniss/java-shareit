package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void create_whenIsCorrectUserDto_thenSaveAndReturnUserDto() {
        UserDto in = generator.nextObject(UserDto.class);
        in.setId(null);
        UserDto created = userService.create(in);
        in.setId(created.getId());
        assertEquals(created, in);
    }

    @Test
    void update_whenUserFound_thenReturnUserDto() {
        UserDto in = generator.nextObject(UserDto.class);
        in.setId(null);
        UserDto created = userService.create(in);
        in.setId(created.getId());
        in.setEmail(generator.nextObject(String.class));
        in.setName(generator.nextObject(String.class));
        UserDto updated = userService.update(in);
        assertEquals(updated, in);
    }

    @Test
    void patch_whenCorrectParams_thenReturnUserDto() {
        UserDto in = generator.nextObject(UserDto.class);
        in.setId(null);
        UserDto created = userService.create(in);
        String correctEmail = "correct@email.com";
        created.setEmail(correctEmail);
        assertEquals(userService.patch(created.getId(), Map.of("email", correctEmail)), created);
    }

    @Test
    void findById_whenUserFound_thenReturnUserDto() {
        UserDto in = generator.nextObject(UserDto.class);
        in.setId(null);
        UserDto created = userService.create(in);
        in.setId(created.getId());
        assertEquals(userService.findById(created.getId()), created);
    }

    @Test
    void findAll() {
        List<UserDto> empty = userService.findAll();
        assertThat(empty).isEmpty();
        UserDto in = generator.nextObject(UserDto.class);
        in.setId(null);
        UserDto created = userService.create(in);
        List<UserDto> all = userService.findAll();
        assertArrayEquals(all.toArray(), List.of(created).toArray());
    }

    @Test
    void delete_whenUserFound_thenDeleteUser() {
        UserDto in = generator.nextObject(UserDto.class);
        in.setId(null);
        UserDto created = userService.create(in);
        userService.delete(created.getId());
        assertThrows(UserNotFoundException.class,
                () -> userService.delete(created.getId()));
    }

}
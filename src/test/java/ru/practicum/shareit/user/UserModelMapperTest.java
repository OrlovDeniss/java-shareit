package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.mapper.UserModelMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserModelMapperTest {

    final UserModelMapper mapper = new UserModelMapper();
    private static final Long ID = 1L;
    private static final String NAME = "name";
    private static final String EMAIL = "email@email.com";
    final User user = User.builder()
            .id(ID)
            .name(NAME)
            .email(EMAIL)
            .build();

    final UserDto userDto = UserDto.builder()
            .id(ID)
            .name(NAME)
            .email(EMAIL)
            .build();

    final UserDtoShort userDtoShort = UserDtoShort.builder()
            .id(ID)
            .build();

    @Test
    void toDtoTest() {
        assertEquals(userDto, mapper.toDto(user));
    }

    @Test
    void toDtoListTest() {
        assertArrayEquals(List.of(userDto).toArray(), mapper.toDto(List.of(user)).toArray());
    }

    @Test
    void toEntityTest() {
        assertEquals(user, mapper.toEntity(userDto));
    }

    @Test
    void toDtoShort() {
        assertEquals(userDtoShort, mapper.toDtoShort(user));
    }

}

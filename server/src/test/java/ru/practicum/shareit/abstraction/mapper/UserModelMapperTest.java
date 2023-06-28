package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserModelMapperTest extends AbstractModelMapperTest<UserDto, UserDto, User> {

    private final Long userId = generator.nextLong();
    private final String userName = generator.nextObject(String.class);
    private final String userEmail = generator.nextObject(String.class);

    private final User user = User.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final UserDtoShort userDtoShort = UserDtoShort.builder()
            .id(userId)
            .build();

    protected UserModelMapperTest() {
        super(UserMapper.INSTANCE);
    }

    @Override
    protected User getEntity() {
        return user;
    }

    @Override
    protected UserDto getDtoIn() {
        return userDto;
    }

    @Override
    protected UserDto getDtoOut() {
        return userDto;
    }

    @Test
    void toDtoShort() {
        assertEquals(UserMapper.INSTANCE.toDtoShort(user), userDtoShort);
    }
}

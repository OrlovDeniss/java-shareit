package ru.practicum.shareit.abstraction.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

class ItemModelMapperTest extends AbstractModelMapperTest<UserDto, UserDto, User> {

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
}

package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UserMapper extends ModelMapper<UserDto, UserDto, User> {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDtoShort toDtoShort(User user);

}

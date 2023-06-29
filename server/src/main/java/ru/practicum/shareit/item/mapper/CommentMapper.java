package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;

@Mapper
public interface CommentMapper extends ModelMapper<CommentDtoIn, CommentDtoOut, Comment> {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Override
    @Mapping(target = "authorName", source = "user.name")
    CommentDtoOut toDto(Comment comment);

}
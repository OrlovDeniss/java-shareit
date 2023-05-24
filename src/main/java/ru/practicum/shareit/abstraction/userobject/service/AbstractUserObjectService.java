package ru.practicum.shareit.abstraction.userobject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

public class AbstractUserObjectService<I extends Identified, O, E extends UserObject>
        extends AbstractUserObjectEntityService<I, O, E>
        implements UserObjectService<I, O> {

    protected AbstractUserObjectService(ModelMapper<I, O, E> mapper,
                                        UserRepository userRepository,
                                        ObjectMapper objectMapper,
                                        UserObjectRepository<E> objectRepository) {
        super(mapper, userRepository, objectMapper, objectRepository);
    }

    @Override
    public O findById(Long objectId) {
        return toDto(findUserObjectById(objectId));
    }

    @Override
    public List<O> findAllByUserId(Long userId) {
        return toDto(findAllUserObjectsByUserId(userId));
    }

    @Override
    public O create(I in, Long userId) {
        return toDto(createUserObject(toEntity(in), userId));
    }

    @Override
    public O update(I in, Long userId) {
        return toDto(updateUserObject(toEntity(in), userId));
    }

    @Override
    public O patch(Long id, Map<String, Object> fields) {
        return toDto(patchUserObject(id, fields));
    }

}
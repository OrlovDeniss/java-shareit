package ru.practicum.shareit.abstraction.userobject;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.general.EntityNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.exception.user.UserOwnsObjectException;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractUserObjectEntityService<I extends Identified, O, E extends UserObject>
        extends AbstractService<I, O, E> {

    private final UserObjectRepository<E> objectRepository;

    protected AbstractUserObjectEntityService(ModelMapper<I, O, E> mapper,
                                              UserRepository userRepository,
                                              ObjectMapper objectMapper,
                                              UserObjectRepository<E> objectRepository) {
        super(mapper, userRepository, objectMapper);
        this.objectRepository = objectRepository;
    }

    public E findUserObjectById(Long objectId) {
        E e = objectRepository.findById(objectId).orElseThrow(EntityNotFoundException::new);
        log.info("findObjectById: {}.", e);
        return e;
    }

    public List<E> findAllUserObjectsByUserId(Long userId) {
        List<E> list = objectRepository.findAllByUserId(userId);
        log.info("findAllObjectsByUserId userId = {}: list = {}.", userId, list);
        return list;
    }

    public E createUserObject(E e, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        e.setUser(user);
        E savedE = objectRepository.save(e);
        log.info("create/updateUserObject: id = {}.", savedE.getId());
        return savedE;
    }

    public E updateUserObject(E e, Long userId) {
        return createUserObject(e, userId);
    }

    public E patchUserObject(Long objectId,
                             Map<String, Object> newFields) {
        E oldE = objectRepository.findById(objectId).orElseThrow(EntityNotFoundException::new);
        E newE = tryUpdateFields(oldE, newFields);
        E updated = objectRepository.save(newE);
        log.info("patchUserObject: {}.", updated);
        return updated;
    }

    public boolean userExistsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public void throwWhenUserNotOwnObject(Long objectId, Long userId) {
        E e = objectRepository.findById(objectId).orElseThrow(EntityNotFoundException::new);
        if (!e.getUser().getId().equals(userId)) {
            throw new UserOwnsObjectException(
                    String.format("Объект id = %d не принадлежит пользователю id = %d", objectId, userId));
        }
    }

}
package ru.practicum.shareit.abstraction.userobject.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UserOwnsObjectException;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractUserObjectService<D, E extends UserObject>
        extends AbstractService<D, E> implements UserObjectService<D> {

    private final UserObjectRepository<E> objectRepo;
    private final UserRepository userRepo;

    protected AbstractUserObjectService(ModelMapper<D, E> mapper,
                                        UserObjectRepository<E> objectRepo,
                                        UserRepository userRepo) {
        super(mapper);
        this.objectRepo = objectRepo;
        this.userRepo = userRepo;
    }

    @Override
    public D findById(Long objectId) {
        E e = objectRepo.findById(objectId).orElseThrow(EntityNotFoundException::new);
        log.info("findById: {}.", e);
        return mapper.toDto(e);
    }

    @Override
    public List<D> findAllByUserId(Long userId) {
        List<E> list = objectRepo.findAllByUserId(userId);
        log.info("findAllByUserId = {}: list = {}.", userId, list);
        return mapper.toDto(list);
    }

    @Override
    public D create(D d, Long userId) {
        E e = mapper.toEntity(d);
        User user = userRepo.findById(userId).orElseThrow(UserNotFoundException::new);
        e.setUser(user);
        E savedE = objectRepo.save(e);
        log.info("create: id = {}.", savedE.getId());
        return mapper.toDto(savedE);
    }

    @Override
    public D update(D d, Long userId) {
        E e = mapper.toEntity(d);
        User user = userRepo.findById(userId).orElseThrow(UserNotFoundException::new);
        e.setUser(user);
        E updatedE = objectRepo.update(e);
        log.info("update: {}.", updatedE);
        return findById(e.getId());
    }

    @Override
    public D patch(Long objectId, Map<String, Object> newFields, Long userId) {
        userOwnsObjectOrThrow(objectId, userId);
        D oldD = findById(objectId);
        D newD = tryUpdateFields(oldD, newFields);
        D updated = update(newD, userId);
        log.info("patch: {}.", updated);
        return updated;
    }

    @Override
    public void delete(Long objectIid, Long userId) {
        userOwnsObjectOrThrow(objectIid, userId);
        objectRepo.deleteUserObject(objectIid, userId);
        log.info("Удален: id = {}.", objectIid);
    }

    @Override
    public boolean userExistsById(Long userId) {
        return userRepo.existsById(userId);
    }

    @Override
    public boolean objectExistsById(Long objectId) {
        return objectRepo.existsById(objectId);
    }

    private void userOwnsObjectOrThrow(Long objectId, Long userId) {
        E e = objectRepo.findById(objectId).orElseThrow(EntityNotFoundException::new);
        User user = userRepo.findById(userId).orElseThrow(UserNotFoundException::new);
        if (!e.getUser().equals(user)) {
            throw new UserOwnsObjectException(
                    String.format("Объект id = %d не принадлежит пользователю id = %d", objectId, userId));
        }
    }

    protected UserRepository getUserRepository() {
        return userRepo;
    }

    protected UserObjectRepository<E> getSuperObjectRepository() {
        return objectRepo;
    }

    protected abstract UserObjectRepository<E> getObjectRepository();

}
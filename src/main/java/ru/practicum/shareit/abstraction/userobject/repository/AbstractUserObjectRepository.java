package ru.practicum.shareit.abstraction.userobject.repository;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.abstraction.repository.AbstractRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractUserObjectRepository<E extends UserObject>
        extends AbstractRepository<E> implements UserObjectRepository<E> {

    protected final Map<Long, List<Long>> userObjects = new HashMap<>();

    @Override
    public E save(E e) {
        E saved = super.save(e);
        putUserObject(saved.getUser().getId(), saved.getId());
        return saved;
    }

    @Override
    public void deleteUserObject(Long objectId, Long userId) {
        super.delete(objectId);
        removeUserObject(userId, objectId);
    }

    @Override
    public List<E> findAllByUserId(Long userId) {
        userObjects.computeIfAbsent(userId, object -> new ArrayList<>());
        return findALlWhereIdIn(userObjects.get(userId));
    }

    @Override
    public List<E> findALlWhereIdIn(List<Long> objectsId) {
        List<E> objects = new ArrayList<>();
        for (Long id : objectsId) {
            objects.add(super.findById(id).orElseThrow(EntityNotFoundException::new));
        }
        return objects;
    }

    private void putUserObject(Long userId, Long objectId) {
        userObjects.computeIfAbsent(userId, object -> new ArrayList<>());
        List<Long> objects = userObjects.get(userId);
        objects.add(objectId);
        userObjects.put(userId, objects);
    }

    private void removeUserObject(Long userId, Long objectId) {
        userObjects.computeIfAbsent(userId, object -> new ArrayList<>());
        List<Long> objects = userObjects.get(userId);
        objects.remove(objectId);
        userObjects.put(userId, objects);
    }
}
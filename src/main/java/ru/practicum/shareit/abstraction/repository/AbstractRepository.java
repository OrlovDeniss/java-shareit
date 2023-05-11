package ru.practicum.shareit.abstraction.repository;

import ru.practicum.shareit.abstraction.model.Entity;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.*;

public abstract class AbstractRepository<E extends Entity> implements Repository<E> {

    protected final HashMap<Long, E> data = new HashMap<>();
    private long idCounter = 1;

    @Override
    public E save(E e) {
        e.setId(idCounter++);
        data.put(e.getId(), e);
        return e;
    }

    @Override
    public E update(E e) {
        if (existsById(e.getId())) {
            data.put(e.getId(), e);
            return e;
        }
        throw new EntityNotFoundException(e.getId() + " не существует!");
    }

    @Override
    public Optional<E> findById(Long id) {
        if (existsById(id)) {
            return Optional.ofNullable(data.get(id));
        }
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        data.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return data.containsKey(id);
    }

    @Override
    public List<E> findAllWhereIdIn(List<Long> id) {
        List<E> list = new ArrayList<>();
        for (Long i : id) {
            if (existsById(i)) {
                list.add(data.get(i));
            }
        }
        return list;
    }

}

package ru.practicum.shareit.abstraction.userobject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.abstraction.model.Entity;
import ru.practicum.shareit.abstraction.userobject.service.UserObjectService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractUserObjectController<D extends Entity> implements UserObjectController<D> {

    private final UserObjectService<D> service;

    @GetMapping("{id}")
    public D get(@PathVariable @Positive Long id,
                 @RequestHeader(value = USER_ID) @Positive Long userId) {
        objectExistsOrThrow(id);
        userExistsOrThrow(userId);
        return service.findById(id);
    }

    @PostMapping
    public D add(@Valid @RequestBody D d,
                 @RequestHeader(value = USER_ID) @Positive Long userId) {
        userExistsOrThrow(userId);
        return service.create(d, userId);
    }

    @PutMapping
    public D update(@Valid @RequestBody D d,
                    @RequestHeader(value = USER_ID) @Positive Long userId) {
        objectExistsOrThrow(d.getId());
        userExistsOrThrow(userId);
        return service.update(d, userId);
    }

    @PatchMapping("{id}")
    public D patch(@PathVariable @Positive Long id,
                   @RequestBody Map<String, Object> fields,
                   @RequestHeader(value = USER_ID) @Positive Long userId) {
        objectExistsOrThrow(id);
        userExistsOrThrow(userId);
        return service.patch(id, fields, userId);
    }

    @GetMapping
    public List<D> findAllByUserId(@RequestHeader(value = USER_ID) @Positive Long userId) {
        userExistsOrThrow(userId);
        return service.findAllByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id,
                       @RequestHeader(value = USER_ID) @Positive Long userId) {
        objectExistsOrThrow(id);
        userExistsOrThrow(userId);
        service.delete(id, userId);
    }

    private void userExistsOrThrow(Long userId) {
        if (!service.userExistsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь id = %d не найден!", userId));
        }
    }

    private void objectExistsOrThrow(Long id) {
        if (!service.objectExistsById(id)) {
            throw new EntityNotFoundException(String.format("Объект id = %d не найден!", id));
        }
    }

    protected UserObjectService<D> getSuperService() {
        return service;
    }

    protected abstract UserObjectService<D> getService();

}
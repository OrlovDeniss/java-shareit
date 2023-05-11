package ru.practicum.shareit.abstraction.userobject.service;

import java.util.List;
import java.util.Map;

public interface UserObjectService<D> {

    D findById(Long id);

    List<D> findAllByUserId(Long userId);

    D create(D d, Long userId);

    D update(D d, Long userId);

    D patch(Long id, Map<String, Object> fields, Long userId);

    void delete(Long id, Long userId);

    boolean userExistsById(Long userId);

    boolean objectExistsById(Long id);

}
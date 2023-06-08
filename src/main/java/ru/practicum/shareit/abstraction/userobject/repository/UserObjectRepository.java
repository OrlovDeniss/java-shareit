package ru.practicum.shareit.abstraction.userobject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.practicum.shareit.abstraction.model.UserObject;

import java.util.List;

@NoRepositoryBean
public interface UserObjectRepository<E extends UserObject> extends JpaRepository<E, Long> {

    List<E> findAllByUserId(Long userId);

}
package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.abstraction.repository.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends Repository<User> {

    List<User> findAll();

    boolean existsByEmail(String email);

}

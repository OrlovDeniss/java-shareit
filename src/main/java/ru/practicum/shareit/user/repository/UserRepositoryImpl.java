package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.abstraction.repository.AbstractRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl extends AbstractRepository<User> implements UserRepository {

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsByEmail(String email) {
        return data.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}

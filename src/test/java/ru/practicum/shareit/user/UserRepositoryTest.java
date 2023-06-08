package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    UserRepository userRepository;

    final User user = User.builder()
            .name("user1")
            .email("user1@user.ru")
            .build();

    @BeforeEach
    void setUp() {
        entityManager.persistAndFlush(user);
    }

    @Test
    void existByEmail_whenExists_thenTrue() {
        boolean existsByEmail = userRepository.existsByEmail(user.getEmail());
        assertTrue(existsByEmail);
    }

    @Test
    void existByEmail_whenNotExists_thenFalse() {
        boolean existsByEmail = userRepository.existsByEmail("abrabra@arbarba123.com");
        assertFalse(existsByEmail);
    }

}
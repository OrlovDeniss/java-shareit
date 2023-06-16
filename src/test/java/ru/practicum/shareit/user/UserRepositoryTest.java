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
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    private final User user1 = User.builder()
            .name("user1")
            .email("user1@user.ru")
            .build();

    private final User user2 = User.builder()
            .name("user2")
            .email("user2@user.ru")
            .build();

    @BeforeEach
    void setUp() {
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
    }

    @Test
    void existByEmailAndIdNot_whenExists_thenTrue() {
        boolean existByEmailAndIdNo = userRepository.existsByEmailAndIdNot(user1.getEmail(), user2.getId());
        assertTrue(existByEmailAndIdNo);
    }

    @Test
    void existByEmailAndIdNot_whenNotExists_thenFalse() {
        boolean existByEmailAndIdNo = userRepository.existsByEmailAndIdNot("abrabra@arbarba123.com", user1.getId());
        assertFalse(existByEmailAndIdNo);
    }

}
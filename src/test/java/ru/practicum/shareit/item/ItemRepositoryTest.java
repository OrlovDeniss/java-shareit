package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ItemRepository itemRepository;

    final User user = User.builder()
            .name("user1")
            .email("user1@one.ru")
            .build();

    final Item item = Item.builder()
            .name("item1")
            .description("description1")
            .available(true)
            .user(user)
            .build();

    @BeforeEach
    void setUp() {
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(item);
    }

    @Test
    void findByIdWithUser() {
        Optional<Item> foundItem = itemRepository.findByIdWithUser(item.getId());
        assertThat(foundItem).isPresent();
    }

    @Test
    void findByIdWithUserAndComments() {
        Optional<Item> foundItem = itemRepository.findByIdWithUserAndComments(item.getId());
        assertThat(foundItem).isPresent();
    }

    @Test
    void findAllByUserIdWithComments() {
        Page<Item> foundItems = itemRepository.findAllByUserIdWithComments(user.getId(), Pageable.ofSize(10));
        assertThat(foundItems.toList()).hasSize(1);
    }

}
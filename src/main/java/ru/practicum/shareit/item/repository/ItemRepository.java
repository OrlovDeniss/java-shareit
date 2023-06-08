package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public interface ItemRepository extends UserObjectRepository<Item> {

    @Query("SELECT itm FROM Item itm " +
            "JOIN FETCH itm.user " +
            "WHERE itm.id = :itemId ")
    Optional<Item> findByIdWithUser(Long itemId);

    @Query("SELECT itm FROM Item itm " +
            "LEFT JOIN FETCH itm.user " +
            "LEFT JOIN FETCH itm.comments " +
            "WHERE itm.id = :itemId ")
    Optional<Item> findByIdWithUserAndComments(Long itemId);

    @EntityGraph(attributePaths = {"comments"})
    @Query("SELECT itm FROM Item itm " +
            "WHERE itm.user.id = :userId ")
    Page<Item> findAllByUserIdWithComments(Long userId, Pageable pageable);

    Page<Item> findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(String name,
                                                                                                String description,
                                                                                                Pageable pageable);

    void deleteByUserId(Long userId);

}
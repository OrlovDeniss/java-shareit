package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
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

    @Query("SELECT itm FROM Item itm " +
            "LEFT JOIN FETCH itm.comments " +
            "WHERE itm.user.id = :userId ")
    List<Item> findAllByUserIdWithComments(Long userId);

    List<Item> findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(String name,
                                                                                                String description);

    void deleteByUserId(Long userId);

}
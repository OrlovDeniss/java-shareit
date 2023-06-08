package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends UserObjectRepository<Request> {

    @Query("SELECT r FROM Request r " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.id = :requestId ")
    Optional<Request> findByIdWithItems(Long requestId);

    @Query("SELECT r FROM Request r " +
            "LEFT JOIN FETCH r.items " +
            "WHERE r.user.id = :userId ")
    List<Request> findAllByOwnerIdWithItems(Long userId);

    @EntityGraph(attributePaths = {"items"})
    @Query("SELECT r FROM Request r " +
            "WHERE r.user.id <> :userId ")
    Page<Request> findAllOtherRequestsByUserId(Long userId, Pageable pageable);

}
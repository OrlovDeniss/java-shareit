package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends UserObjectRepository<Booking> {

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE b.id = :bookingId ")
    Optional<Booking> findByIdWithUserAndItem(Long bookingId);

    @Query("SELECT CASE WHEN (COUNT(b) > 0) THEN TRUE ELSE FALSE END " +
            "FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.user.id = :userId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < CURRENT_TIMESTAMP ")
    boolean existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent(Long itemId,
                                                                                     Long userId);

    Optional<Booking> findBookingByUserIdAndItemIdAndStatus(Long userId, Long itemId, Status status);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.user.id = :userId ")
    Page<Booking> findAllByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.user.id = :userId")
    Page<Booking> findAllByOwnerId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.start > CURRENT_TIMESTAMP " +
            "AND b.user.id = :userId ")
    Page<Booking> findAllByUserIdWhereStartIsAfterCurrentTimestamp(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.start > CURRENT_TIMESTAMP " +
            "AND b.item.user.id = :userId ")
    Page<Booking> findAllByOwnerIdWhereStartIsAfterCurrentTimestamp(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.end < CURRENT_TIMESTAMP " +
            "AND b.user.id = :userId ")
    Page<Booking> findAllByUserIdWhereEndBeforeCurrent(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.end < CURRENT_TIMESTAMP " +
            "AND b.item.user.id = :userId ")
    Page<Booking> findAllByOwnerIdWhereEndBeforeCurrent(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "AND b.user.id = :userId ")
    Page<Booking> findAllByUserIdWhereCurrentTimestampBetweenStartAndEnd(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "AND b.item.user.id = :userId ")
    Page<Booking> findAllByOwnerIdWhereCurrentTimestampBetweenStartAndEnd(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.status = :status " +
            "AND b.user.id = :userId ")
    Page<Booking> findBookingsByUserIdWhereStatus(Long userId, Status status, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "user"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.status = :status " +
            "AND b.item.user.id = :userId ")
    Page<Booking> findBookingsByOwnerIdWhereStatus(Long userId, Status status, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT id, user_id bookerId, item_id itemId FROM booking " +
                    "WHERE item_id = :itemId AND start_time <= CURRENT_TIMESTAMP AND status = 'APPROVED' " +
                    "ORDER BY start_time DESC " +
                    "LIMIT 1 ")
    Optional<BookingShort> findLastBookingByItemId(Long itemId);

    @Query(nativeQuery = true,
            value = "SELECT id, user_id bookerId, item_id itemId FROM booking " +
                    "WHERE item_id = :itemId AND start_time >= CURRENT_TIMESTAMP AND status = 'APPROVED'" +
                    "ORDER BY start_time " +
                    "LIMIT 1 ")
    Optional<BookingShort> findNextBookingByItemId(Long itemId);

    @Query(nativeQuery = true,
            value = "SELECT b.id as id, b.user_id as bookerId, b.item_id as itemId " +
                    "FROM booking b " +
                    "JOIN (SELECT item_id, MAX(start_time) max " +
                    "      FROM booking " +
                    "      WHERE item_id IN (select id from item WHERE item.user_id = :userId) " +
                    "      AND start_time <= CURRENT_TIMESTAMP AND status = 'APPROVED'" +
                    "      group by item_id) as lasts on b.item_id = lasts.item_id and b.start_time = lasts.max")
    List<BookingShort> findLastBookingsByUserId(Long userId);

    @Query(nativeQuery = true,
            value = "SELECT b.id as id, b.user_id as bookerId, b.item_id as itemId " +
                    "FROM booking b " +
                    "JOIN (SELECT item_id, MIN(start_time) min " +
                    "      FROM booking " +
                    "      WHERE item_id IN (select id from item WHERE item.user_id = :userId) " +
                    "      AND start_time >= CURRENT_TIMESTAMP AND status = 'APPROVED'" +
                    "      group by item_id) as nexts on b.item_id = nexts.item_id and b.start_time = nexts.min")
    List<BookingShort> findNextBookingsByUserId(Long userId);
}
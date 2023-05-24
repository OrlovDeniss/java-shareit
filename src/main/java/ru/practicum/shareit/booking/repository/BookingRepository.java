package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
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

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE b.user.id = :userId ")
    List<Booking> findAllByUserId(Long userId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE b.item.user.id = :userId")
    List<Booking> findAllByUserIdAndUserOwnsItem(Long userId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE b.start > CURRENT_TIMESTAMP " +
            "AND b.user.id = :userId ")
    List<Booking> findAllByUserIdWhereStartIsAfterCurrentTimestamp(Long userId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE b.start > CURRENT_TIMESTAMP " +
            "AND b.item.user.id = :userId ")
    List<Booking> findAllByUserIdWhereStartIsAfterCurrentTimestampAndUserOwnsItem(Long userId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE b.end < CURRENT_TIMESTAMP " +
            "AND b.user.id = :userId ")
    List<Booking> findAllByUserIdWhereEndBeforeCurrent(Long userId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE b.end < CURRENT_TIMESTAMP " +
            "AND b.item.user.id = :userId ")
    List<Booking> findAllByUserIdWhereEndBeforeCurrentAndUserOwnsItem(Long userId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "AND b.user.id = :userId ")
    List<Booking> findAllByUserIdWhereCurrentTimestampBetweenStartAndEnd(Long userId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "AND b.item.user.id = :userId ")
    List<Booking> findAllByUserIdWhereCurrentTimestampBetweenStartAndEndAndUserOwnsItem(Long userId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE b.status = :status " +
            "AND b.user.id = :userId ")
    List<Booking> findBookingsByUserIdWhereStatus(Long userId, Status status, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.item " +
            "WHERE b.status = :status " +
            "AND b.item.user.id = :userId ")
    List<Booking> findBookingsByUserIdWhereStatusAndUserOwnsItem(Long userId, Status status, Sort sort);

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
package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    BookingRepository bookingRepository;

    private static final LocalDateTime START = LocalDateTime.of(2077, 1, 1, 1, 1, 1);
    private static final LocalDateTime END = LocalDateTime.of(2078, 1, 1, 1, 1, 1);

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

    final Booking futureBooking = Booking.builder()
            .start(START)
            .end(END)
            .item(item)
            .user(user)
            .status(Status.WAITING)
            .build();

    final Booking futureBookingIsApproved = Booking.builder()
            .start(START)
            .end(END)
            .item(item)
            .user(user)
            .status(Status.APPROVED)
            .build();

    final Booking currentBooking = Booking.builder()
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().plusDays(1))
            .item(item)
            .user(user)
            .status(Status.APPROVED)
            .build();

    final Booking oldBooking = Booking.builder()
            .start(START.minusYears(1000))
            .end(END.minusYears(1000))
            .item(item)
            .user(user)
            .status(Status.APPROVED)
            .build();

    @BeforeEach
    void setUp() {
        entityManager.persist(user);
        entityManager.persist(item);
    }

    @Test
    void findByIdWithUserAndItem() {
        entityManager.persist(currentBooking);
        Optional<Booking> foundBooking = bookingRepository.findByIdWithUserAndItem(currentBooking.getId());
        assertThat(foundBooking).isPresent();
    }

    @Test
    void existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent_returnTrue() {
        entityManager.persist(futureBooking);
        boolean foundBooking = bookingRepository
                .existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent(item.getId(), user.getId());
        assertFalse(foundBooking);
    }

    @Test
    void existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent_returnFalse() {
        entityManager.persist(oldBooking);
        boolean foundBooking = bookingRepository
                .existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent(item.getId(), user.getId());
        assertTrue(foundBooking);
    }

    @Test
    void findAllByUserId() {
        entityManager.persist(futureBooking);
        Page<Booking> foundBooking = bookingRepository
                .findAllByUserId(user.getId(), Pageable.ofSize(10));
        assertThat(foundBooking.toList()).hasSize(1);
    }

    @Test
    void findAllByOwnerId() {
        entityManager.persist(futureBooking);
        Page<Booking> foundBooking = bookingRepository
                .findAllByOwnerId(user.getId(), Pageable.ofSize(10));
        assertThat(foundBooking.toList()).hasSize(1);
    }

    @Test
    void findAllByUserIdWhereStartIsAfterCurrentTimestamp() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        Page<Booking> foundBooking = bookingRepository
                .findAllByUserIdWhereStartIsAfterCurrentTimestamp(user.getId(), Pageable.ofSize(10));
        assertThat(foundBooking.toList()).hasSize(1);
    }

    @Test
    void findAllByOwnerIdWhereStartIsAfterCurrentTimestamp() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        Page<Booking> foundBooking = bookingRepository
                .findAllByOwnerIdWhereStartIsAfterCurrentTimestamp(user.getId(), Pageable.ofSize(10));
        assertThat(foundBooking.toList()).hasSize(1);
    }

    @Test
    void findAllByUserIdWhereEndBeforeCurrent() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        Page<Booking> foundBooking = bookingRepository
                .findAllByUserIdWhereEndBeforeCurrent(user.getId(), Pageable.ofSize(10));
        assertThat(foundBooking.toList()).hasSize(1);
    }

    @Test
    void findAllByOwnerIdWhereEndBeforeCurrent() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        Page<Booking> foundBooking = bookingRepository
                .findAllByOwnerIdWhereEndBeforeCurrent(user.getId(), Pageable.ofSize(10));
        assertThat(foundBooking.toList()).hasSize(1);
    }

    @Test
    void findAllByUserIdWhereCurrentTimestampBetweenStartAndEnd() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Page<Booking> foundBooking = bookingRepository
                .findAllByUserIdWhereCurrentTimestampBetweenStartAndEnd(user.getId(), Pageable.ofSize(10));
        assertThat(foundBooking.toList()).hasSize(1);
    }

    @Test
    void findBookingsByUserIdWhereStatus() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Page<Booking> foundBooking = bookingRepository
                .findBookingsByUserIdWhereStatus(user.getId(), Status.WAITING, Pageable.ofSize(10));
        assertThat(foundBooking.toList()).hasSize(1);
        assertThat(foundBooking.toList().get(0)).isEqualTo(futureBooking);
    }

    @Test
    void findBookingsByOwnerIdWhereStatus() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Page<Booking> foundBooking = bookingRepository
                .findBookingsByOwnerIdWhereStatus(user.getId(), Status.WAITING, Pageable.ofSize(10));
        assertThat(foundBooking.toList()).hasSize(1);
        assertThat(foundBooking.toList().get(0)).isEqualTo(futureBooking);
    }

    @Test
    void findLastBookingByItemId() {
        entityManager.persist(futureBooking);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Optional<BookingShort> foundBooking = bookingRepository
                .findLastBookingByItemId(item.getId());
        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get()).hasFieldOrPropertyWithValue("id", currentBooking.getId());
    }

    @Test
    void findNextBookingByItemId() {
        entityManager.persist(futureBookingIsApproved);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        Optional<BookingShort> foundBooking = bookingRepository
                .findNextBookingByItemId(item.getId());
        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get()).hasFieldOrPropertyWithValue("id", futureBookingIsApproved.getId());
    }

    @Test
    void findLastBookingsByUserId() {
        entityManager.persist(futureBookingIsApproved);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        List<BookingShort> foundBookings = bookingRepository
                .findLastBookingsByUserId(user.getId());
        assertThat(foundBookings).hasSize(1);
    }

    @Test
    void findNextBookingsByUserId() {
        entityManager.persist(futureBookingIsApproved);
        entityManager.persist(oldBooking);
        entityManager.persist(currentBooking);
        List<BookingShort> foundBookings = bookingRepository
                .findNextBookingsByUserId(user.getId());
        assertThat(foundBookings).hasSize(1);
    }
}
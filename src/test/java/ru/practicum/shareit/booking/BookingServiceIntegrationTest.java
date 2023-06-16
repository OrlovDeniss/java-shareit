package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingServiceImpl bookingService;

    private final EasyRandom generator = new EasyRandom();
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");

    private final int from = 0;
    private final int size = 10;

    @Test
    void create() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        in.setItemId(item.getId());
        BookingDtoOut created = bookingService.create(in, booker.getId());
        assertNotNull(created.getId());
        assertEquals(in.getStart(), created.getStart());
        assertEquals(in.getEnd(), created.getEnd());
        assertEquals(item.getId(), created.getItem().getId());
        assertEquals(item.getName(), created.getItem().getName());
        assertEquals(item.getDescription(), created.getItem().getDescription());
        assertEquals(item.getAvailable(), created.getItem().isAvailable());
        assertNull(created.getItem().getRequestId());
        assertEquals(item.getUser().getId(), created.getItem().getOwnerId());
        assertEquals(booker.getId(), created.getUser().getId());
        assertEquals(Status.WAITING, created.getStatus());
    }

    @Test
    void findById() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setStart(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)));
        in.setEnd(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)));
        in.setItemId(item.getId());
        BookingDtoOut created = bookingService.create(in, booker.getId());
        BookingDtoOut out = bookingService.findById(created.getId(), owner.getId());
        assertEquals(created, out);
    }

    @Test
    void patch() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        BookingDtoOut out = bookingService.patch(booking.getId(), owner.getId(), true);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.APPROVED, out.getStatus());
    }

    @Test
    void findAllByUserIdAndState_whenStateIsAll_thanReturnBookings() {
        State state = State.ALL;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByUserIdAndState_whenStateIsFuture_thanReturnBookings() {
        State state = State.FUTURE;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByUserIdAndState_whenStateIsCurrent_thanReturnBookings() {
        State state = State.CURRENT;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByUserIdAndState_whenStateIsPast_thanReturnBookings() {
        State state = State.PAST;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByUserIdAndState_whenStateIsWaiting_thanReturnBookings() {
        State state = State.WAITING;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByUserIdAndState_whenStateIsRejected_thanReturnBookings() {
        State state = State.REJECTED;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.REJECTED)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, booker.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.REJECTED, out.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_whenStateIsAll_thanReturnBookings() {
        State state = State.ALL;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_whenStateIsFuture_thanReturnBookings() {
        State state = State.FUTURE;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_whenStateIsCurrent_thanReturnBookings() {
        State state = State.CURRENT;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_whenStateIsPast_thanReturnBookings() {
        State state = State.PAST;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().minusDays(2).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().minusDays(1).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_whenStateIsWaiting_thanReturnBookings() {
        State state = State.WAITING;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.WAITING, out.getStatus());
    }

    @Test
    void findAllByOwnerIdAndState_whenStateIsRejected_thanReturnBookings() {
        State state = State.REJECTED;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = itemRepository.save(Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .user(owner)
                .build());
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(format)))
                .end(LocalDateTime.parse(LocalDateTime.now().plusDays(2).format(format)))
                .item(item)
                .user(booker)
                .status(Status.REJECTED)
                .build());
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, owner.getId(), state);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertNotNull(out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(item.getId(), out.getItem().getId());
        assertEquals(item.getName(), out.getItem().getName());
        assertEquals(item.getDescription(), out.getItem().getDescription());
        assertEquals(item.getAvailable(), out.getItem().isAvailable());
        assertNull(out.getItem().getRequestId());
        assertEquals(item.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booker.getId(), out.getUser().getId());
        assertEquals(Status.REJECTED, out.getStatus());
    }

}
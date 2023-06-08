package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.booking.*;
import ru.practicum.shareit.util.exception.general.UnsupportedStateException;
import ru.practicum.shareit.util.exception.item.ItemNotAvailableException;
import ru.practicum.shareit.util.exception.item.ItemNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookingServiceTest {

    @MockBean
    ItemRepository itemRepository;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    UserRepository userRepository;
    @Autowired
    BookingServiceImpl bookingService;
    @Captor
    ArgumentCaptor<Booking> bookingCaptor;

    private static final LocalDateTime START = LocalDateTime.of(2077, 1, 1, 1, 1, 1);
    private static final LocalDateTime END = LocalDateTime.of(2078, 1, 1, 1, 1, 1);

    final BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .id(1L)
            .start(START)
            .end(END)
            .itemId(1L)
            .build();

    final User user = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@one.ru")
            .build();

    final Item item = Item.builder()
            .id(1L)
            .name("item1")
            .description("description1")
            .available(true)
            .user(user)
            .build();

    final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(1L)
            .name("item1")
            .description("description1")
            .available(true)
            .ownerId(1L)
            .build();

    final Booking booking = Booking.builder()
            .id(1L)
            .start(START)
            .end(END)
            .item(item)
            .user(user)
            .status(Status.WAITING)
            .build();

    final BookingDtoOut referenceBookingDtoOut = BookingDtoOut.builder()
            .id(1L)
            .start(START)
            .end(END)
            .item(itemDtoShort)
            .booker(new UserDtoShort(1L))
            .status(Status.WAITING)
            .build();

    @Test
    void create_whenStartAfterEndOrEquals_throwBookingTimeConstraintException() {
        bookingDtoIn.setEnd(LocalDateTime.MIN);

        assertThrows(BookingTimeConstraintException.class,
                () -> bookingService.create(bookingDtoIn, 1L));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenUserNotFound_throwUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.create(bookingDtoIn, 1L));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenItemNotFound_throwItemNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(bookingDtoIn, 1L));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenItemNotAvailable_throwItemNotAvailableException() {
        item.setAvailable(false);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.create(bookingDtoIn, 1L));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenUserOwnerItem_throwBookingOwnerItemException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(BookingOwnerItemException.class,
                () -> bookingService.create(bookingDtoIn, 1L));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenBookingAlreadyExists_throwBookingAlreadyExistsException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findBookingByUserIdAndItemIdAndStatus(anyLong(), anyLong(), any(Status.class)))
                .thenReturn(Optional.ofNullable(booking));

        assertThrows(BookingAlreadyExistsException.class,
                () -> bookingService.create(bookingDtoIn, 2L));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenIsCorrectRequest_thenReturnBookingDtoOut() {
        Long userId = 2L;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findBookingByUserIdAndItemIdAndStatus(anyLong(), anyLong(), any(Status.class)))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDtoOut bookingDtoOut = bookingService.create(bookingDtoIn, userId);
        assertEquals(bookingDtoOut, referenceBookingDtoOut);

        verify(bookingRepository)
                .save(bookingCaptor.capture());
        Booking bookingCaptorValue = bookingCaptor.getValue();
        assertEquals(bookingCaptorValue, booking);
    }

    @Test
    void findById_whenUserNotOwnBookingItemAndNotBooker_throwBookingAccessException() {
        Long bookingId = 1L;
        Long userId = 2L;

        when(bookingRepository.findByIdWithUserAndItem(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        assertThrows(BookingAccessException.class,
                () -> bookingService.findById(bookingId, userId));
    }

    @Test
    void findById_whenUserOwnBookingItemOrBooker_thenReturnBookingDtoOut() {
        Long bookingId = 1L;
        Long userId = 1L;

        when(bookingRepository.findByIdWithUserAndItem(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDtoOut bookingDtoOut = bookingService.findById(bookingId, userId);
        assertEquals(bookingDtoOut, referenceBookingDtoOut);
    }

    @Test
    void patch_whenUserNotFound_throwUserNotFoundException() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;

        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.patch(bookingId, userId, approved));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_whenBookingNotFound_throwBookingNotFoundException() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findByIdWithUserAndItem(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.patch(bookingId, userId, approved));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_whenBookingStatusIsApproved_throwBookingAlreadyApprovedException() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;
        booking.setStatus(Status.APPROVED);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findByIdWithUserAndItem(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(BookingAlreadyApprovedException.class,
                () -> bookingService.patch(bookingId, userId, approved));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_whenUserNotOwnerBookingItem_throwBookingAccessException() {
        Long bookingId = 1L;
        Long userId = 2L;
        Boolean approved = true;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findByIdWithUserAndItem(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(BookingAccessException.class,
                () -> bookingService.patch(bookingId, userId, approved));

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_whenCorrect_returnBookingDtoOut() {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;
        referenceBookingDtoOut.setStatus(Status.APPROVED);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findByIdWithUserAndItem(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .then(returnsFirstArg());

        BookingDtoOut bookingDtoOut = bookingService.patch(bookingId, userId, approved);
        assertEquals(bookingDtoOut, referenceBookingDtoOut);

        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void findAllByUserIdAndState_whenUserNotFound_throwUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.findAllByUserIdAndState(1, 1, 1L, State.ALL));
    }

    @Test
    void findAllByUserIdAndState_whenUserFoundAndStateAll_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.ALL;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByUserIdAndState_whenUserFoundAndStateFuture_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.FUTURE;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByUserIdWhereStartIsAfterCurrentTimestamp(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByUserIdAndState_whenUserFoundAndStateCurrent_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.CURRENT;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByUserIdWhereCurrentTimestampBetweenStartAndEnd(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByUserIdAndState_whenUserFoundAndStatePast_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.PAST;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByUserIdWhereEndBeforeCurrent(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByUserIdAndState_whenUserFoundAndStateWaiting_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.WAITING;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findBookingsByUserIdWhereStatus(anyLong(), eq(Status.WAITING), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByUserIdAndState_whenUserFoundAndStateRejected_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.REJECTED;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findBookingsByUserIdWhereStatus(anyLong(), eq(Status.REJECTED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByUserIdAndState_whenUserFoundAndStatusUnknown_thanThrowUnsupportedStateException() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.UNKNOWN;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.findAllByUserIdAndState(from, size, userId, state));
    }

    @Test
    void findAllByOwnerIdAndState_whenUserNotFound_throwUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.findAllByOwnerIdAndState(1, 1, 1L, State.ALL));
    }

    @Test
    void findAllByOwnerIdAndState_whenUserFound_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.ALL;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByOwnerIdAndState_whenUserFoundAndStateFuture_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.FUTURE;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByOwnerIdWhereStartIsAfterCurrentTimestamp(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByOwnerIdAndState_whenUserFoundAndStateCurrent_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.CURRENT;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByOwnerIdWhereCurrentTimestampBetweenStartAndEnd(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByOwnerIdAndState_whenUserFoundAndStatePast_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.PAST;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByOwnerIdWhereEndBeforeCurrent(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByOwnerIdAndState_whenUserFoundAndStateWaiting_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.WAITING;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findBookingsByOwnerIdWhereStatus(anyLong(), eq(Status.WAITING), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByOwnerIdAndState_whenUserFoundAndStateRejected_thanReturnListBookingDtoOut() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.REJECTED;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findBookingsByOwnerIdWhereStatus(anyLong(), eq(Status.REJECTED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, userId, state);
        assertThat(bookingDtoOuts).hasSize(1);
        assertEquals(bookingDtoOuts.get(0), referenceBookingDtoOut);
    }

    @Test
    void findAllByOwnerIdAndState_whenUserFoundAndStatusUnknown_thanThrowUnsupportedStateException() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        State state = State.UNKNOWN;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.findAllByUserIdAndState(from, size, userId, state));
    }
}
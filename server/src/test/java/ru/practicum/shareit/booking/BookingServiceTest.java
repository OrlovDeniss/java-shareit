package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.finder.*;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.booking.*;
import ru.practicum.shareit.util.exception.general.UnsupportedStateException;
import ru.practicum.shareit.util.exception.item.ItemNotAvailableException;
import ru.practicum.shareit.util.exception.item.ItemNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.pagerequest.PageRequester;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private BookingServiceImpl bookingService;

    private final EasyRandom generator = new EasyRandom();

    private final int from = 0;
    private final int size = 10;

    @BeforeEach
    void setUp() {
        bookingService = Mockito.mock(BookingServiceImpl.class, CALLS_REAL_METHODS);
    }

    @Test
    void create_whenItemNotAvailable_thenThrowItemNotAvailableException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        when(itemRepository.existsByIdAndAvailableIsFalse(anyLong()))
                .thenReturn(true);
        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.create(in, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenUserOwnItem_thenThrowBookingOwnerItemException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(true);
        assertThrows(BookingItemOwnerException.class,
                () -> bookingService.create(in, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenUserNotFound_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        when(itemRepository.existsByIdAndAvailableIsFalse(anyLong()))
                .thenReturn(false);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> bookingService.create(in, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenItemNotFound_thenThrowItemNotFoundException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        User user = generator.nextObject(User.class);
        when(itemRepository.existsByIdAndAvailableIsFalse(anyLong()))
                .thenReturn(false);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(in, user.getId()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void create_whenIsCorrectRequest_thenReturnBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        User user = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        when(itemRepository.existsByIdAndAvailableIsFalse(anyLong()))
                .thenReturn(false);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .then(returnsFirstArg());
        BookingDtoOut created = bookingService.create(in, user.getId());
        assertEquals(in.getId(), created.getId());
        assertEquals(in.getStart(), created.getStart());
        assertEquals(in.getEnd(), created.getEnd());
        assertEquals(item.getId(), created.getItem().getId());
        assertEquals(item.getName(), created.getItem().getName());
        assertEquals(item.getDescription(), created.getItem().getDescription());
        assertEquals(item.getAvailable(), created.getItem().isAvailable());
        assertNotNull(created.getItem().getRequestId());
        assertEquals(item.getUser().getId(), created.getItem().getOwnerId());
        assertEquals(user.getId(), created.getUser().getId());
        assertEquals(Status.WAITING, created.getStatus());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void update_whenUserNotBooker_thenThrowBookingAccessException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        when(bookingRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(BookingUpdateAccessException.class,
                () -> bookingService.update(in, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void update_whenUserNotFound_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        when(bookingRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> bookingService.update(in, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void update_whenItemNotFound_thenThrowItemNotFoundException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        User user = generator.nextObject(User.class);
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        when(bookingRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.update(in, generator.nextLong()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void update_whenCorrect_thenReturnBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        User user = generator.nextObject(User.class);
        BookingDtoIn in = generator.nextObject(BookingDtoIn.class);
        in.setEnd(LocalDateTime.MAX);
        Item item = generator.nextObject(Item.class);
        when(bookingRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .then(returnsFirstArg());
        BookingDtoOut updated = bookingService.update(in, generator.nextLong());
        assertEquals(in.getId(), updated.getId());
        assertEquals(in.getStart(), updated.getStart());
        assertEquals(in.getEnd(), updated.getEnd());
        assertNotNull(updated.getItem());
        assertNotNull(updated.getUser());
        assertEquals(Status.WAITING, updated.getStatus());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void findById_whenUserNotOwnBookingItemAndNotBooker_throwBookingAccessException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        when(bookingRepository.existsByBookingIdWithUserIdOrItemUserId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(BookingAccessException.class,
                () -> bookingService.findById(generator.nextLong(), generator.nextLong()));
        verify(bookingRepository, never())
                .findByIdWithUserAndItem(anyLong());
    }

    @Test
    void findById_whenUserOwnBookingItemOrBooker_thenReturnBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        Booking booking = generator.nextObject(Booking.class);
        when(bookingRepository.existsByBookingIdWithUserIdOrItemUserId(anyLong(), anyLong()))
                .thenReturn(true);
        when(bookingRepository.findByIdWithUserAndItem(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        BookingDtoOut found = bookingService.findById(generator.nextLong(), generator.nextLong());
        assertNotNull(found.getId());
        assertNotNull(found.getStart());
        assertNotNull(found.getEnd());
        assertNotNull(found.getItem());
        assertNotNull(found.getUser());
        assertNotNull(found.getStatus());
        verify(bookingRepository, times(1))
                .findByIdWithUserAndItem(anyLong());
    }

    @Test
    void patch_whenPatchNotItemOwner_thenThrowBookingStatusPatchException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        when(bookingRepository.existsByIdAndItemUserId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(BookingStatusPatchException.class,
                () -> bookingService.patch(generator.nextLong(), generator.nextLong(), generator.nextBoolean()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_whenBookingStatusIsNotWaiting_thenThrowBookingPatchConstraintException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        when(bookingRepository.existsByIdAndItemUserId(anyLong(), anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsByIdAndStatus(anyLong(), eq(Status.WAITING)))
                .thenReturn(false);
        assertThrows(BookingPatchConstraintException.class,
                () -> bookingService.patch(generator.nextLong(), generator.nextLong(), generator.nextBoolean()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_whenBookingNotFound_thenThrowBookingNotFoundException() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        when(bookingRepository.existsByIdAndItemUserId(anyLong(), anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsByIdAndStatus(anyLong(), eq(Status.WAITING)))
                .thenReturn(true);
        when(bookingRepository.findByIdWithUserAndItem(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.patch(generator.nextLong(), generator.nextLong(), generator.nextBoolean()));
        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void patch_whenCorrectAndApprovedTrue_thenReturnBookingDtoOutWithStatusApproved() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        Booking booking = generator.nextObject(Booking.class);
        when(bookingRepository.existsByIdAndItemUserId(anyLong(), anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsByIdAndStatus(anyLong(), eq(Status.WAITING)))
                .thenReturn(true);
        when(bookingRepository.findByIdWithUserAndItem(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .then(returnsFirstArg());
        BookingDtoOut patched = bookingService.patch(booking.getId(), generator.nextLong(), true);
        assertEquals(booking.getId(), patched.getId());
        assertEquals(booking.getStart(), patched.getStart());
        assertEquals(booking.getEnd(), patched.getEnd());
        assertEquals(booking.getItem().getId(), patched.getItem().getId());
        assertEquals(booking.getItem().getName(), patched.getItem().getName());
        assertEquals(booking.getItem().getDescription(), patched.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), patched.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), patched.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), patched.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), patched.getUser().getId());
        assertEquals(Status.APPROVED, patched.getStatus());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void patch_whenCorrectAndApprovedFalse_thenReturnBookingDtoOutWithStatusRejected() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        Booking booking = generator.nextObject(Booking.class);
        when(bookingRepository.existsByIdAndItemUserId(anyLong(), anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsByIdAndStatus(anyLong(), eq(Status.WAITING)))
                .thenReturn(true);
        when(bookingRepository.findByIdWithUserAndItem(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .then(returnsFirstArg());
        BookingDtoOut patched = bookingService.patch(booking.getId(), generator.nextLong(), false);
        assertEquals(booking.getId(), patched.getId());
        assertEquals(booking.getStart(), patched.getStart());
        assertEquals(booking.getEnd(), patched.getEnd());
        assertEquals(booking.getItem().getId(), patched.getItem().getId());
        assertEquals(booking.getItem().getName(), patched.getItem().getName());
        assertEquals(booking.getItem().getDescription(), patched.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), patched.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), patched.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), patched.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), patched.getUser().getId());
        assertEquals(Status.REJECTED, patched.getStatus());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void findAllByUserIdAndState_whenUserNotFound_throwUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.findAllByUserIdAndState(from, size, generator.nextLong(), State.ALL));
    }

    @Test
    void findAllByUserIdAndState_whenStateAll_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("all", new All(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByUserId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, generator.nextLong(), State.ALL);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByUserIdAndState_whenStateFuture_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("future", new Future(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllFutureBookingsByUserId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, generator.nextLong(), State.FUTURE);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByUserIdAndState_whenStateCurrent_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("current", new Current(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllCurrentBookingsByUserId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, generator.nextLong(), State.CURRENT);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByUserIdAndState_whenStatePast_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("past", new Past(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllPastBookingsByUserId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, generator.nextLong(), State.PAST);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByUserIdAndState_whenStateWaiting_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("waiting", new Waiting(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByUserIdAndStatus(anyLong(), eq(Status.WAITING), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, generator.nextLong(), State.WAITING);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByUserIdAndState_whenStateRejected_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("rejected", new Rejected(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByUserIdAndStatus(anyLong(), eq(Status.REJECTED), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByUserIdAndState(from, size, generator.nextLong(), State.REJECTED);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByUserIdAndState_whenStatusUnknown_thanThrowUnsupportedStateException() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of());
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        assertThrows(UnsupportedStateException.class,
                () -> bookingService.findAllByUserIdAndState(from, size, generator.nextLong(), State.UNKNOWN));
    }

    @Test
    void findAllByOwnerIdAndState_whenUserNotFound_throwUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> bookingService.findAllByOwnerIdAndState(from, size, generator.nextLong(), State.ALL));
    }

    @Test
    void findAllByOwnerIdAndState_whenStateAll_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("all", new All(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByOwnerId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, generator.nextLong(), State.ALL);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByOwnerIdAndState_whenFuture_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("future", new Future(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllFutureBookingsByOwnerId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, generator.nextLong(), State.FUTURE);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByOwnerIdAndState_whenCurrent_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("current", new Current(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllCurrentBookingsByOwnerId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, generator.nextLong(), State.CURRENT);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByOwnerIdAndState_whenStatePast_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("past", new Past(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllPastBookingsByOwnerId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, generator.nextLong(), State.PAST);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByOwnerIdAndState_whenStateWaiting_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("waiting", new Waiting(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByOwnerIdAndStatus(anyLong(),eq(Status.WAITING), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, generator.nextLong(), State.WAITING);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByOwnerIdAndState_whenStateRejected_thanReturnListBookingDtoOut() {
        ReflectionTestUtils.setField(bookingService, "mapper", BookingMapper.INSTANCE);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(bookingService, "repository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of("rejected", new Rejected(bookingRepository)));
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllByOwnerIdAndStatus(anyLong(), eq(Status.REJECTED), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoOut> bookingDtoOuts = bookingService.findAllByOwnerIdAndState(from, size, generator.nextLong(), State.REJECTED);
        assertThat(bookingDtoOuts).hasSize(1);
        BookingDtoOut out = bookingDtoOuts.get(0);
        assertEquals(booking.getId(), out.getId());
        assertEquals(booking.getStart(), out.getStart());
        assertEquals(booking.getEnd(), out.getEnd());
        assertEquals(booking.getItem().getId(), out.getItem().getId());
        assertEquals(booking.getItem().getName(), out.getItem().getName());
        assertEquals(booking.getItem().getDescription(), out.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), out.getItem().isAvailable());
        assertEquals(booking.getItem().getRequest().getId(), out.getItem().getRequestId());
        assertEquals(booking.getUser().getId(), out.getItem().getOwnerId());
        assertEquals(booking.getUser().getId(), out.getUser().getId());
    }

    @Test
    void findAllByOwnerIdAndState_whenStatusUnknown_thanThrowUnsupportedStateException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        FinderStrategyFactory finderStrategyFactory = Mockito.mock(FinderStrategyFactory.class, CALLS_REAL_METHODS);
        ReflectionTestUtils.setField(finderStrategyFactory, "finderStrategyMap", Map.of());
        ReflectionTestUtils.setField(bookingService, "finderStrategyFactory", finderStrategyFactory);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        assertThrows(UnsupportedStateException.class,
                () -> bookingService.findAllByOwnerIdAndState(from, size, generator.nextLong(), State.UNKNOWN));
    }

}
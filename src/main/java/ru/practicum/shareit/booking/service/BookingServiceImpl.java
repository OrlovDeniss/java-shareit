package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.userobject.AbstractUserObjectService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingModelMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.finder.FinderStrategyFactory;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.booking.*;
import ru.practicum.shareit.util.exception.item.ItemNotAvailableException;
import ru.practicum.shareit.util.exception.item.ItemNotFoundException;

import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl extends AbstractUserObjectService<BookingDtoIn, BookingDtoOut, Booking>
        implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final FinderStrategyFactory finderStrategyFactory;

    private final Sort sort = Sort.by("start").descending();


    protected BookingServiceImpl(BookingModelMapper mapper,
                                 UserRepository userRepository,
                                 ObjectMapper objectMapper,
                                 BookingRepository bookingRepository,
                                 ItemRepository itemRepository,
                                 FinderStrategyFactory finderStrategyFactory) {
        super(mapper, userRepository, objectMapper, bookingRepository);
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.finderStrategyFactory = finderStrategyFactory;
    }

    @Override
    @Transactional
    public BookingDtoOut create(BookingDtoIn bookingDtoIn, Long userId) {
        throwWhenStartAfterEndOrEquals(bookingDtoIn);
        throwWhenUserNotFound(userId);
        Item item = itemRepository.findByIdWithUser(bookingDtoIn.getItemId()).orElseThrow(ItemNotFoundException::new);
        throwWhenItemAvailableIsFalse(item);
        throwWhenUserOwnerItem(item, userId);
        throwWhenBookingAlreadyExists(item, userId);
        Booking booking = toEntity(bookingDtoIn);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return toDto(super.createUserObject(booking, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOut findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdWithUserAndItem(bookingId).orElseThrow(BookingNotFoundException::new);
        throwWhenUserNotOwnBookingItemAndNotBooker(booking, userId);
        return toDto(booking);
    }

    @Override
    @Transactional
    public BookingDtoOut patch(Long bookingId, Long userId, Boolean approved) {
        throwWhenUserNotFound(userId);
        Booking booking = bookingRepository.findByIdWithUserAndItem(bookingId).orElseThrow(BookingNotFoundException::new);
        throwWhenBookingStatusIsApproved(booking);
        throwWhenUserNotOwnerBookingItem(booking, userId);
        boolean isApproved = Boolean.TRUE.equals(approved);
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllByUserIdAndState(Integer from, Integer size, Long userId, State state) {
        throwWhenUserNotFound(userId);
        Pageable sortedByStartDesc = PageRequest.of(from / size, size, sort);
        return toDto(finderStrategyFactory.findStrategyByState(state).findAllByUserId(userId, sortedByStartDesc));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllByOwnerIdAndState(Integer from, Integer size, Long ownerId, State state) {
        throwWhenUserNotFound(ownerId);
        Pageable sortedByStartDesc = PageRequest.of(from / size, size, sort);
        return toDto(finderStrategyFactory.findStrategyByState(state).findAllByOwnerId(ownerId, sortedByStartDesc));
    }

    private void throwWhenUserNotOwnerBookingItem(Booking booking, Long userId) {
        boolean isNotOwnerBookingItem = !booking.getItem().getUser().getId().equals(userId);
        if (isNotOwnerBookingItem) {
            throw new BookingAccessException();
        }
    }

    private void throwWhenUserNotOwnBookingItemAndNotBooker(Booking booking, Long userId) {
        boolean isNotOwnerBookingItem = !booking.getItem().getUser().getId().equals(userId);
        boolean isNotBooker = !booking.getUser().getId().equals(userId);
        if (isNotOwnerBookingItem && isNotBooker) {
            throw new BookingAccessException();
        }
    }

    private void throwWhenBookingStatusIsApproved(Booking booking) {
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingAlreadyApprovedException();
        }
    }

    private void throwWhenStartAfterEndOrEquals(BookingDtoIn dtoIn) {
        if (dtoIn.getStart().isAfter(dtoIn.getEnd()) || dtoIn.getStart().equals(dtoIn.getEnd())) {
            throw new BookingTimeConstraintException();
        }
    }

    private void throwWhenItemAvailableIsFalse(Item item) {
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ItemNotAvailableException(String.format("Предмет id = %d не доступен.", item.getId()));
        }
    }

    private void throwWhenUserOwnerItem(Item item, Long userId) {
        if (item.getUser().getId().equals(userId)) {
            throw new BookingOwnerItemException();
        }
    }

    private void throwWhenBookingAlreadyExists(Item item, Long userId) {
        if (bookingRepository.findBookingByUserIdAndItemIdAndStatus(userId, item.getId(), Status.WAITING).isPresent()) {
            throw new BookingAlreadyExistsException();
        }
    }

}
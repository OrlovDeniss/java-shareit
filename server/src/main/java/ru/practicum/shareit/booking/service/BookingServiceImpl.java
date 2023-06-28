package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.finder.FinderStrategyFactory;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.booking.*;
import ru.practicum.shareit.util.exception.item.ItemNotAvailableException;
import ru.practicum.shareit.util.exception.item.ItemNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.pagerequest.PageRequester;

import java.util.List;

@Service
@Transactional
public class BookingServiceImpl extends AbstractService<BookingDtoIn, BookingDtoOut, Booking> implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final FinderStrategyFactory finderStrategyFactory;

    private static final Sort sort = Sort.by("start").descending();

    public BookingServiceImpl(JpaRepository<Booking, Long> repository,
                              ObjectMapper objectMapper,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              FinderStrategyFactory finderStrategyFactory) {
        super(repository, BookingMapper.INSTANCE, objectMapper);
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.finderStrategyFactory = finderStrategyFactory;
    }

    @Override
    public BookingDtoOut create(BookingDtoIn bookingDtoIn, Long userId) {
        throwWhenItemAvailableIsFalseOrNotExists(bookingDtoIn.getItemId());
        throwWhenUserOwnItemOrNotExists(bookingDtoIn.getItemId(), userId);
        return toDto(getRepository().save(mergeToBookingWithStatusWaiting(bookingDtoIn, userId)));
    }

    @Override
    public BookingDtoOut update(BookingDtoIn bookingDtoIn, Long userId) {
        throwWhenUserNotBookerOrNotExists(bookingDtoIn.getId(), userId);
        return toDto(getRepository().save(mergeToBookingWithStatusWaiting(bookingDtoIn, userId)));
    }

    @Transactional(readOnly = true)
    public BookingDtoOut findById(Long bookingId, Long userId) {
        throwWhenUserNotBookerOrNotItemOwnerOrNotExists(bookingId, userId);
        return toDto(getRepository().findByIdWithUserAndItem(bookingId).orElseThrow(BookingNotFoundException::new));
    }

    @Override
    public BookingDtoOut patch(Long bookingId, Long userId, boolean approved) {
        throwWhenBookingPatchNotItemOwner(bookingId, userId);
        throwWhenBookingStatusIsNotWaiting(bookingId);
        Booking booking = getRepository().findByIdWithUserAndItem(bookingId).orElseThrow(BookingNotFoundException::new);
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return toDto(getRepository().save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllByUserIdAndState(Integer from, Integer size, Long userId, State state) {
        throwWhenUserNotExist(userId);
        return toDto(finderStrategyFactory.findStrategyByState(state)
                .findAllByUserId(userId, new PageRequester(from, size, sort)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoOut> findAllByOwnerIdAndState(Integer from, Integer size, Long ownerId, State state) {
        throwWhenUserNotExist(ownerId);
        return toDto(finderStrategyFactory.findStrategyByState(state)
                .findAllByOwnerId(ownerId, new PageRequester(from, size, sort)));
    }

    private Booking mergeToBookingWithStatusWaiting(BookingDtoIn bookingDtoIn, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Item item = itemRepository.findById(bookingDtoIn.getItemId()).orElseThrow(ItemNotFoundException::new);
        Booking booking = toEntity(bookingDtoIn);
        booking.setUser(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    private void throwWhenUserNotExist(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("id = %d не существует!", id));
        }
    }

    private void throwWhenBookingPatchNotItemOwner(Long bookingId, Long userId) {
        if (!getRepository().existsByIdAndItemUserId(bookingId, userId)) {
            throw new BookingStatusPatchException();
        }
    }

    private void throwWhenUserNotBookerOrNotItemOwnerOrNotExists(Long bookingId, Long userId) {
        if (!getRepository().existsByBookingIdWithUserIdOrItemUserId(bookingId, userId)) {
            throw new BookingAccessException();
        }
    }

    private void throwWhenBookingStatusIsNotWaiting(Long bookingId) {
        if (!getRepository().existsByIdAndStatus(bookingId, Status.WAITING)) {
            throw new BookingPatchConstraintException();
        }
    }

    private void throwWhenItemAvailableIsFalseOrNotExists(Long itemId) {
        if (itemRepository.existsByIdAndAvailableIsFalse(itemId)) {
            throw new ItemNotAvailableException(String.format("Предмет id = %d не доступен или не существует.", itemId));
        }
    }

    private void throwWhenUserOwnItemOrNotExists(Long itemId, Long userId) {
        if (itemRepository.existsByIdAndUserId(itemId, userId)) {
            throw new BookingItemOwnerException();
        }
    }

    private void throwWhenUserNotBookerOrNotExists(Long itemId, Long userId) {
        if (!getRepository().existsByIdAndUserId(itemId, userId)) {
            throw new BookingUpdateAccessException();
        }
    }

    private BookingRepository getRepository() {
        return (BookingRepository) repository;
    }

}
package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.CommentDtoIn;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.comment.CommentOwnItemException;
import ru.practicum.shareit.util.exception.comment.CommentWithoutBookingException;
import ru.practicum.shareit.util.exception.item.ItemNotFoundException;
import ru.practicum.shareit.util.exception.request.RequestNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.exception.user.UserOwnsObjectException;
import ru.practicum.shareit.util.pagerequest.PageRequester;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ItemServiceImpl extends AbstractService<ItemDtoIn, ItemDtoOut, Item> implements ItemService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    private static final Sort sort = Sort.by("id").ascending();

    public ItemServiceImpl(JpaRepository<Item, Long> repository,
                           ObjectMapper objectMapper, UserRepository userRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository,
                           RequestRepository requestRepository) {
        super(repository, ItemMapper.INSTANCE, objectMapper);
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemDtoOut create(ItemDtoIn itemDtoIn, Long userId) {
        throwWhenUserNotExist(userId);
        return toDto(getRepository().save(merge(itemDtoIn, userId)));
    }

    @Override
    public ItemDtoOut update(ItemDtoIn itemDtoIn, Long userId) {
        throwWhenUserNotExist(userId);
        throwWhenUserNotOwnItem(itemDtoIn.getId(), userId);
        return toDto(getRepository().save(merge(itemDtoIn, userId)));
    }

    @Override
    public ItemDtoOut patch(Long itemId, Map<String, Object> newFields, Long userId) {
        throwWhenUserNotExist(userId);
        throwWhenUserNotOwnItem(itemId, userId);
        Item item = getRepository().findById(itemId).orElseThrow(ItemNotFoundException::new);
        return toDto(getRepository().save(tryUpdateFields(item, newFields)));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoOut findById(Long itemId, Long userId) {
        throwWhenUserNotExist(userId);
        Item item = getRepository().findByIdWithUserAndComments(itemId).orElseThrow(ItemNotFoundException::new);
        ItemDtoOut itemDtoOut = toDto(item);
        boolean isOwner = Objects.equals(item.getUser().getId(), userId);
        if (isOwner) {
            bookingRepository.findLastBookingByItemId(itemId).ifPresent(b -> itemDtoOut.setLastBooking(toDto(b)));
            bookingRepository.findNextBookingByItemId(itemId).ifPresent(b -> itemDtoOut.setNextBooking(toDto(b)));
        }
        return itemDtoOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> findAllByUserId(Integer from, Integer size, Long userId) {
        throwWhenUserNotExist(userId);
        List<Item> items = getRepository().findAllByUserIdWithComments(userId, new PageRequester(from, size, sort)).toList();
        List<BookingShort> lastBookings = bookingRepository.findLastBookingsByUserId(userId);
        List<BookingShort> nextBookings = bookingRepository.findNextBookingsByUserId(userId);
        return merge(items, lastBookings, nextBookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> searchByNameOrDescription(Integer from, Integer size, String text) {
        return toDto(getRepository().findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(
                        text,
                        text,
                        new PageRequester(from, size, sort))
                .toList());
    }

    @Override
    public CommentDtoOut createComment(Long itemId, Long userId, CommentDtoIn commentDtoIn) {
        throwWhenUserCommentOwnItem(itemId, userId);
        throwWhenUserCommentWithoutBooking(itemId, userId);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Item item = getRepository().findByIdWithUser(itemId).orElseThrow(ItemNotFoundException::new);
        Comment comment = toEntity(commentDtoIn);
        comment.setUser(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        item.getComments().add(comment);
        return toDto(commentRepository.save(comment));
    }

    private Item merge(ItemDtoIn itemDtoIn, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Item item = toEntity(itemDtoIn);
        item.setUser(user);
        Long requestId = itemDtoIn.getRequestId();
        if (Objects.nonNull(requestId)) {
            Request request = requestRepository.findByIdWithItems(requestId).orElseThrow(RequestNotFoundException::new);
            item.setRequest(request);
            if (Objects.isNull(request.getItems())) {
                request.setItems(List.of(item));
            } else {
                request.getItems().add(item);
            }
        }
        return item;
    }

    private ArrayList<ItemDtoOut> merge(List<Item> items,
                                        List<BookingShort> lastBookings,
                                        List<BookingShort> nextBookings) {
        Map<Long, ItemDtoOut> map = new HashMap<>();
        for (Item item : items) {
            ItemDtoOut itemDtoOut = toDto(item);
            map.put(item.getId(), itemDtoOut);
        }
        lastBookings.forEach(last -> map.get(last.getItemId()).setLastBooking(toDto(last)));
        nextBookings.forEach(next -> map.get(next.getItemId()).setNextBooking(toDto(next)));
        return new ArrayList<>(map.values());
    }

    private void throwWhenUserCommentWithoutBooking(Long itemId, Long userId) {
        if (!bookingRepository.existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent(itemId, userId)) {
            throw new CommentWithoutBookingException();
        }
    }

    private void throwWhenUserCommentOwnItem(Long itemId, Long userId) {
        if (getRepository().existsByIdAndUserId(itemId, userId)) {
            throw new CommentOwnItemException();
        }
    }

    private void throwWhenUserNotExist(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("id = %d не существует!", id));
        }
    }

    public void throwWhenUserNotOwnItem(Long itemId, Long userId) {
        if (!getRepository().existsByIdAndUserId(itemId, userId)) {
            throw new UserOwnsObjectException(
                    String.format("Item id = %d не принадлежит пользователю id = %d", itemId, userId));
        }
    }

    private ItemRepository getRepository() {
        return (ItemRepository) repository;
    }

    private Comment toEntity(CommentDtoIn commentDtoIn) {
        return CommentMapper.INSTANCE.toEntity(commentDtoIn);
    }

    private CommentDtoOut toDto(Comment comment) {
        return CommentMapper.INSTANCE.toDto(comment);
    }

    private BookingDtoShort toDto(BookingShort bookingShort) {
        return BookingMapper.INSTANCE.toDtoShort(bookingShort);
    }

}
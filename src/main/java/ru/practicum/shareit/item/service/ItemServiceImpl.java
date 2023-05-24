package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.userobject.service.AbstractUserObjectService;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingModelMapper;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoIn;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.mapper.CommentModelMapper;
import ru.practicum.shareit.item.mapper.ItemModelMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.comment.CommentWithoutBookingException;
import ru.practicum.shareit.util.exception.item.ItemNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.exception.user.UserOwnsObjectException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ItemServiceImpl extends AbstractUserObjectService<ItemDtoIn, ItemDtoOut, Item> implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentModelMapper commentModelMapper;
    private final BookingModelMapper bookingModelMapper;

    protected ItemServiceImpl(ItemModelMapper mapper,
                              UserRepository userRepository,
                              ObjectMapper objectMapper,
                              ItemRepository itemRepository,
                              CommentRepository commentRepository,
                              BookingRepository bookingRepository,
                              CommentModelMapper commentModelMapper,
                              BookingModelMapper bookingModelMapper) {
        super(mapper, userRepository, objectMapper, itemRepository);
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.commentModelMapper = commentModelMapper;
        this.bookingModelMapper = bookingModelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoOut findById(Long itemId, Long userId) {
        Item item = itemRepository.findByIdWithUserAndComments(itemId).orElseThrow(ItemNotFoundException::new);
        ItemDtoOut itemDtoOut = toDto(item);
        boolean isOwner = Objects.equals(item.getUser().getId(), userId);
        if (isOwner) {
            bookingRepository.findLastBookingByItemId(itemId).ifPresent(b -> itemDtoOut.setLastBooking(toDtoShort(b)));
            bookingRepository.findNextBookingByItemId(itemId).ifPresent(b -> itemDtoOut.setNextBooking(toDtoShort(b)));
        }
        if (Objects.nonNull(item.getComments())) {
            itemDtoOut.setComments(commentModelMapper.toDto(item.getComments()));
        }
        return itemDtoOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> findAllByUserId(Long userId) {
        List<Item> items = itemRepository.findAllByUserIdWithComments(userId);
        List<BookingShort> lastBookings = bookingRepository.findLastBookingsByUserId(userId);
        List<BookingShort> nextBookings = bookingRepository.findNextBookingsByUserId(userId);
        return mergeToItemDtoOut(items, lastBookings, nextBookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> search(String text) {
        return toDto(itemRepository
                .findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(text, text));
    }

    @Override
    @Transactional
    public CommentDtoOut createComment(Long itemId, Long userId, CommentDtoIn commentDtoIn) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Item item = itemRepository.findByIdWithUser(itemId).orElseThrow(ItemNotFoundException::new);
        throwWhenUserCommentOwnItem(userId, item);
        throwWhenUserCommentWithoutBooking(itemId, userId);
        Comment comment = commentModelMapper.toEntity(commentDtoIn);
        comment.setUser(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        item.getComments().add(comment);
        return toDto(commentRepository.save(comment));
    }

    private void throwWhenUserCommentWithoutBooking(Long itemId, Long userId) {
        if (!bookingRepository.existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent(itemId, userId)) {
            throw new CommentWithoutBookingException();
        }
    }

    private void throwWhenUserCommentOwnItem(Long userId, Item item) {
        boolean isOwner = item.getUser().getId().equals(userId);
        if (isOwner) {
            throw new UserOwnsObjectException("Пользователь не может добавить отзыв к своему предмету.");
        }
    }

    private ArrayList<ItemDtoOut> mergeToItemDtoOut(List<Item> items,
                                                    List<BookingShort> lastBookings,
                                                    List<BookingShort> nextBookings) {
        Map<Long, ItemDtoOut> map = new HashMap<>();
        for (Item item : items) {
            ItemDtoOut dto = toDto(item);
            dto.setComments(commentModelMapper.toDto(item.getComments()));
            map.put(item.getId(), dto);
        }
        lastBookings.forEach(last -> map.get(last.getItemId()).setLastBooking(toDtoShort(last)));
        nextBookings.forEach(next -> map.get(next.getItemId()).setNextBooking(toDtoShort(next)));
        return new ArrayList<>(map.values());
    }

    private CommentDtoOut toDto(Comment comment) {
        return commentModelMapper.toDto(comment);
    }

    private BookingDtoShort toDtoShort(BookingShort bookingShort) {
        return bookingModelMapper.toDtoShort(bookingShort);
    }

}
package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoIn;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.comment.CommentWithoutBookingException;
import ru.practicum.shareit.util.exception.item.ItemNotFoundException;
import ru.practicum.shareit.util.exception.request.RequestNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.exception.user.UserOwnsObjectException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    RequestRepository requestRepository;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    CommentRepository commentRepository;
    @Autowired
    ItemServiceImpl itemService;

    final User user = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@one.ru")
            .build();

    final Request request = Request.builder()
            .id(1L)
            .description("description")
            .created(LocalDateTime.now())
            .build();

    final ItemDtoIn itemDtoIn = ItemDtoIn.builder()
            .id(1L)
            .name("item1")
            .description("description")
            .available(true)
            .build();

    final Item item = Item.builder()
            .name("item1")
            .description("description")
            .available(true)
            .user(user)
            .comments(new ArrayList<>())
            .build();

    final ItemDtoOut referenceItemDtoOut = ItemDtoOut.builder()
            .id(1L)
            .name("item1")
            .description("description")
            .available(true)
            .comments(new ArrayList<>())
            .build();

    final CommentDtoIn commentDtoIn = new CommentDtoIn("commentText");
    final Comment comment = new Comment(1L, "commentText", user, item, LocalDateTime.of(2025, 1, 1, 1, 1));
    final CommentDtoOut commentDtoOut = new CommentDtoOut(1L, "commentText", "user1", LocalDateTime.of(2025, 1, 1, 1, 1));

    final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    final BookingShort nextBooking = factory.createProjection(BookingShort.class);
    final BookingShort lastBooking = factory.createProjection(BookingShort.class);

    @Test
    void create_whenUserNotFound_throwUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> itemService.create(itemDtoIn, 1L));

        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void create_whenRequestIdIsNull_thenReturnItemDtoOut() {
        Long userId = 1L;
        referenceItemDtoOut.setComments(null);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());

        ItemDtoOut createdItemDtoOut = itemService.create(itemDtoIn, userId);
        assertEquals(createdItemDtoOut, referenceItemDtoOut);

        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void create_whenRequestNotFound_thenThrowRequestNotFoundException() {
        referenceItemDtoOut.setRequestId(request.getId());
        itemDtoIn.setId(1L);
        itemDtoIn.setRequestId(5L);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findByIdWithItems(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class,
                () -> itemService.create(itemDtoIn, 1L));

        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void create_whenRequestFound_thenReturnItemDtoOut() {
        Long userId = 1L;
        referenceItemDtoOut.setRequestId(request.getId());
        referenceItemDtoOut.setComments(null);
        itemDtoIn.setId(1L);
        itemDtoIn.setRequestId(1L);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findByIdWithItems(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());

        ItemDtoOut createdItemDtoOut = itemService.create(itemDtoIn, userId);
        assertEquals(createdItemDtoOut, referenceItemDtoOut);

        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void findById_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> itemService.findById(1L, 1L));

        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void findById_whenItemNotFound_thenThrowItemNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithUserAndComments(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.findById(1L, 1L));

        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void findById_whenUserIsOwner_returnItemDtoOut() {
        Long itemId = 1L;
        Long userId = 1L;
        item.setId(itemId);
        Long nextId = 4L;
        Long lastId = 3L;
        Long nextBookerId = 2L;
        Long lastBookerId = 2L;
        nextBooking.setId(nextId);
        nextBooking.setBookerId(nextBookerId);
        nextBooking.setItemId(1L);
        lastBooking.setId(lastId);
        lastBooking.setBookerId(lastBookerId);
        lastBooking.setItemId(1L);
        referenceItemDtoOut.setLastBooking(new BookingDtoShort(lastId, lastBookerId));
        referenceItemDtoOut.setNextBooking(new BookingDtoShort(nextId, nextBookerId));

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithUserAndComments(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingByItemId(itemId))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findNextBookingByItemId(itemId))
                .thenReturn(Optional.of(nextBooking));

        ItemDtoOut itemDtoOut = itemService.findById(itemId, userId);
        assertEquals(itemDtoOut, referenceItemDtoOut);

        verify(itemRepository, times(1))
                .findByIdWithUserAndComments(anyLong());
    }

    @Test
    void findById_whenUserIsNotOwner_returnItemDtoOut() {
        Long itemId = 1L;
        Long userId = 2L;
        item.setId(itemId);
        nextBooking.setId(2L);
        nextBooking.setBookerId(2L);
        nextBooking.setItemId(1L);
        lastBooking.setId(1L);
        lastBooking.setBookerId(2L);
        lastBooking.setItemId(1L);

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithUserAndComments(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingByItemId(itemId))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findNextBookingByItemId(itemId))
                .thenReturn(Optional.of(nextBooking));

        ItemDtoOut itemDtoOut = itemService.findById(itemId, userId);
        assertEquals(itemDtoOut, referenceItemDtoOut);

        verify(itemRepository, times(1))
                .findByIdWithUserAndComments(anyLong());
        verify(bookingRepository, never())
                .findLastBookingByItemId(anyLong());
        verify(bookingRepository, never())
                .findNextBookingByItemId(anyLong());
    }

    @Test
    void findAllByUserId_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> itemService.findAllByUserId(1, 1, 1L));

        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void findAllByUserId_whenParamsIsCorrect_thenReturnItemDtoOut() {
        Long userId = 1L;
        Integer from = 1;
        Integer size = 10;
        item.setId(1L);
        Long nextId = 4L;
        Long lastId = 3L;
        Long nextBookerId = 2L;
        Long lastBookerId = 2L;
        nextBooking.setId(nextId);
        nextBooking.setBookerId(nextBookerId);
        nextBooking.setItemId(1L);
        lastBooking.setId(lastId);
        lastBooking.setBookerId(lastBookerId);
        lastBooking.setItemId(1L);
        referenceItemDtoOut.setLastBooking(new BookingDtoShort(lastId, lastBookerId));
        referenceItemDtoOut.setNextBooking(new BookingDtoShort(nextId, nextBookerId));

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllByUserIdWithComments(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findLastBookingsByUserId(anyLong()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findNextBookingsByUserId(anyLong()))
                .thenReturn(List.of(nextBooking));

        List<ItemDtoOut> allByUserId = itemService.findAllByUserId(from, size, userId);
        assertArrayEquals(allByUserId.toArray(), List.of(referenceItemDtoOut).toArray());
    }

    @Test
    void searchByNameOrDescription() {
        Integer from = 1;
        Integer size = 1;
        String text = "item";
        item.setId(referenceItemDtoOut.getId());
        referenceItemDtoOut.setComments(null);

        when(itemRepository.findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(
                anyString(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDtoOut> itemDtoOuts = itemService.searchByNameOrDescription(from, size, text);
        assertThat(itemDtoOuts).hasSize(1);
        assertEquals(itemDtoOuts.get(0), referenceItemDtoOut);
    }

    @Test
    void createComment_whenUserNotFound_thenThrowUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> itemService.createComment(1L, 1L, commentDtoIn));

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void createComment_whenItemNotFound_thenThrowItemNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.createComment(1L, 1L, commentDtoIn));

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void createComment_whenUserCommentOwnItem_thenThrowUserOwnsObjectException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(UserOwnsObjectException.class,
                () -> itemService.createComment(1L, 1L, commentDtoIn));

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void createComment_whenUserCommentWithoutBooking_thenThrowCommentWithoutBookingException() {
        Long notOwnerId = 31L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent(anyLong(), anyLong()))
                .thenReturn(false);

        assertThrows(CommentWithoutBookingException.class,
                () -> itemService.createComment(1L, notOwnerId, commentDtoIn));

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void createComment_whenChecksIsOk_thenReturnCommentDtoOut() {
        Long notOwnerId = 31L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent(anyLong(), anyLong()))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDtoOut comment1 = itemService.createComment(1L, notOwnerId, commentDtoIn);
        assertEquals(comment1, commentDtoOut);

        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }
}
package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.config.ObjectMapperConfig;
import ru.practicum.shareit.util.exception.comment.CommentOwnItemException;
import ru.practicum.shareit.util.exception.comment.CommentWithoutBookingException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.exception.user.UserOwnsObjectException;
import ru.practicum.shareit.util.pagerequest.PageRequester;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    private ItemServiceImpl itemService;
    private final EasyRandom generator = new EasyRandom();
    private final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    private final BookingShort next = factory.createProjection(BookingShort.class);
    private final BookingShort last = factory.createProjection(BookingShort.class);

    private final int from = 0;
    private final int size = 10;

    @BeforeEach
    void setUp() {
        itemService = Mockito.mock(ItemServiceImpl.class, CALLS_REAL_METHODS);
    }

    @Test
    void create_whenUserNotExist_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> itemService.create(generator.nextObject(ItemDtoIn.class), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void create_whenRequestIdIsNull_thenReturnItemDtoOut() {
        ReflectionTestUtils.setField(itemService, "mapper", ItemMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        User owner = generator.nextObject(User.class);
        ItemDtoIn in = generator.nextObject(ItemDtoIn.class);
        in.setRequestId(null);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());
        ItemDtoOut created = itemService.create(in, owner.getId());
        assertNotNull(created.getId());
        assertEquals(in.getName(), created.getName());
        assertEquals(in.getDescription(), created.getDescription());
        assertEquals(in.getAvailable(), created.getAvailable());
        assertEquals(in.getRequestId(), created.getRequestId());
        assertNull(created.getLastBooking());
        assertNull(created.getNextBooking());
        assertNull(created.getComments());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void create_whenRequestIdIsNotNull_thenReturnItemDtoOut() {
        ReflectionTestUtils.setField(itemService, "mapper", ItemMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(itemService, "requestRepository", requestRepository);
        User owner = generator.nextObject(User.class);
        ItemDtoIn in = generator.nextObject(ItemDtoIn.class);
        Request request = generator.nextObject(Request.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(requestRepository.findByIdWithItems(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());
        ItemDtoOut created = itemService.create(in, owner.getId());
        assertNotNull(created.getId());
        assertEquals(in.getName(), created.getName());
        assertEquals(in.getDescription(), created.getDescription());
        assertEquals(in.getAvailable(), created.getAvailable());
        assertEquals(request.getId(), created.getRequestId());
        assertNull(created.getLastBooking());
        assertNull(created.getNextBooking());
        assertNull(created.getComments());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void update_whenUserNotExist_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> itemService.update(generator.nextObject(ItemDtoIn.class), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void update_whenUserNotOwnItem_thenThrowUserOwnsObjectException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(UserOwnsObjectException.class,
                () -> itemService.update(generator.nextObject(ItemDtoIn.class), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void update() {
        ReflectionTestUtils.setField(itemService, "mapper", ItemMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(itemService, "requestRepository", requestRepository);
        User owner = generator.nextObject(User.class);
        ItemDtoIn in = generator.nextObject(ItemDtoIn.class);
        Request request = generator.nextObject(Request.class);
        in.setRequestId(request.getId());
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsByIdAndUserId(in.getId(), owner.getId()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(requestRepository.findByIdWithItems(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());
        ItemDtoOut updated = itemService.update(in, owner.getId());
        assertNotNull(updated.getId());
        assertEquals(in.getName(), updated.getName());
        assertEquals(in.getDescription(), updated.getDescription());
        assertEquals(in.getAvailable(), updated.getAvailable());
        assertEquals(in.getRequestId(), updated.getRequestId());
        assertNull(updated.getLastBooking());
        assertNull(updated.getNextBooking());
        assertNull(updated.getComments());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void patch_whenUserNotExist_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> itemService.patch(generator.nextLong(), Map.of(), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void patch_whenUserNotOwnItem_thenThrowUserOwnsObjectException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(UserOwnsObjectException.class,
                () -> itemService.patch(generator.nextLong(), Map.of(), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void patch() {
        ReflectionTestUtils.setField(itemService, "mapper", ItemMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        ReflectionTestUtils.setField(itemService, "objectMapper", new ObjectMapperConfig().objectMapper());
        User owner = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .then(returnsFirstArg());
        ItemDtoOut updated = itemService.patch(item.getId(), Map.of(), owner.getId());
        assertNotNull(updated.getId());
        assertEquals(item.getName(), updated.getName());
        assertEquals(item.getDescription(), updated.getDescription());
        assertEquals(item.getAvailable(), updated.getAvailable());
        assertEquals(item.getRequest().getId(), updated.getRequestId());
        assertNull(updated.getLastBooking());
        assertNull(updated.getNextBooking());
        assertNotNull(updated.getComments());
        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void findById_whenUserNotExist_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> itemService.findById(generator.nextLong(), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void findById_whenUserIsOwner_returnItemDtoOut() {
        ReflectionTestUtils.setField(itemService, "mapper", ItemMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        User owner = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        next.setId(generator.nextLong());
        next.setItemId(item.getId());
        next.setBookerId(generator.nextLong());
        last.setId(generator.nextLong());
        last.setItemId(item.getId());
        last.setBookerId(generator.nextLong());
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithUserAndComments(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingByItemId(anyLong()))
                .thenReturn(Optional.of(last));
        when(bookingRepository.findNextBookingByItemId(anyLong()))
                .thenReturn(Optional.of(next));
        ItemDtoOut found = itemService.findById(item.getId(), owner.getId());
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(item.getRequest().getId(), found.getRequestId());
        assertEquals(last.getId(), found.getLastBooking().getId());
        assertEquals(last.getBookerId(), found.getLastBooking().getBookerId());
        assertEquals(next.getId(), found.getNextBooking().getId());
        assertEquals(next.getBookerId(), found.getNextBooking().getBookerId());
        verify(itemRepository, times(1))
                .findByIdWithUserAndComments(anyLong());
        verify(bookingRepository, times(1))
                .findLastBookingByItemId(anyLong());
        verify(bookingRepository, times(1))
                .findNextBookingByItemId(anyLong());
    }

    @Test
    void findById_whenUserIsNotOwner_returnItemDtoOut() {
        ReflectionTestUtils.setField(itemService, "mapper", ItemMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        User owner = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByIdWithUserAndComments(anyLong()))
                .thenReturn(Optional.of(item));
        ItemDtoOut found = itemService.findById(item.getId(), generator.nextLong());
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(item.getRequest().getId(), found.getRequestId());
        assertNull(found.getLastBooking());
        assertNull(found.getNextBooking());
        verify(itemRepository, times(1))
                .findByIdWithUserAndComments(anyLong());
        verify(bookingRepository, never())
                .findLastBookingByItemId(anyLong());
        verify(bookingRepository, never())
                .findNextBookingByItemId(anyLong());
    }

    @Test
    void findAllByUserId_whenUserNotExist_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> itemService.findAllByUserId(generator.nextInt(), generator.nextInt(), generator.nextLong()));
        verify(itemRepository, never())
                .save(any(Item.class));
    }

    @Test
    void findAllByUserId() {
        ReflectionTestUtils.setField(itemService, "mapper", ItemMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        User owner = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        next.setId(generator.nextLong());
        next.setItemId(item.getId());
        next.setBookerId(generator.nextLong());
        last.setId(generator.nextLong());
        last.setItemId(item.getId());
        last.setBookerId(generator.nextLong());
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllByUserIdWithComments(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findLastBookingsByUserId(anyLong()))
                .thenReturn(List.of(last));
        when(bookingRepository.findNextBookingsByUserId(anyLong()))
                .thenReturn(List.of(next));
        List<ItemDtoOut> foundList = itemService.findAllByUserId(from, size, owner.getId());
        assertThat(foundList).hasSize(1);
        ItemDtoOut found = foundList.get(0);
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(item.getRequest().getId(), found.getRequestId());
        assertEquals(last.getId(), found.getLastBooking().getId());
        assertEquals(last.getBookerId(), found.getLastBooking().getBookerId());
        assertEquals(next.getId(), found.getNextBooking().getId());
        assertEquals(next.getBookerId(), found.getNextBooking().getBookerId());
    }

    @Test
    void searchByNameOrDescription() {
        ReflectionTestUtils.setField(itemService, "mapper", ItemMapper.INSTANCE);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        Item item = generator.nextObject(Item.class);
        when(itemRepository.findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(
                anyString(), anyString(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        List<ItemDtoOut> foundList = itemService.searchByNameOrDescription(from, size, item.getDescription());
        assertThat(foundList).hasSize(1);
        ItemDtoOut found = foundList.get(0);
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(item.getRequest().getId(), found.getRequestId());
        assertNull(found.getLastBooking());
        assertNull(found.getNextBooking());
    }

    @Test
    void createComment_whenUserCommentOwnItem_thenReturnCommentOwnItemException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(true);
        assertThrows(CommentOwnItemException.class,
                () -> itemService.createComment(generator.nextLong(), generator.nextLong(), generator.nextObject(CommentDtoIn.class)));
        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void createComment_whenUserCommentWithoutBooking_thenReturnCommentOwnItemException() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);
        when(bookingRepository.existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent(anyLong(), anyLong()))
                .thenReturn(false);
        assertThrows(CommentWithoutBookingException.class,
                () -> itemService.createComment(generator.nextLong(), generator.nextLong(), generator.nextObject(CommentDtoIn.class)));
        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    void createComment() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        ReflectionTestUtils.setField(itemService, "repository", itemRepository);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        CommentDtoIn in = generator.nextObject(CommentDtoIn.class);
        User user = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        when(itemRepository.existsByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);
        when(bookingRepository.existsBookingByItemIdAndUserIdAndStatusIsApprovedAndEndTimeBeforeCurrent(anyLong(), anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithUser(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class)))
                .then(returnsFirstArg());
        CommentDtoOut created = itemService.createComment(item.getId(), user.getId(), in);
        assertNull(created.getId()); // database responsibility
        assertEquals(in.getText(), created.getText());
        assertEquals(user.getName(), created.getAuthorName());
        assertNotNull(created.getCreated());
        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }

}
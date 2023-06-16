package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.comment.CommentDtoIn;
import ru.practicum.shareit.item.dto.comment.CommentDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemServiceImpl itemService;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void create_whenRequestIdIsNull_thenReturnItemDtoOut() {
        User owner = userRepository.save(generator.nextObject(User.class));
        ItemDtoIn in = generator.nextObject(ItemDtoIn.class);
        in.setRequestId(null);
        ItemDtoOut created = itemService.create(in, owner.getId());
        assertNotNull(created.getId());
        assertEquals(in.getName(), created.getName());
        assertEquals(in.getDescription(), created.getDescription());
        assertEquals(in.getAvailable(), created.getAvailable());
        assertNull(created.getLastBooking());
        assertNull(created.getNextBooking());
        assertNull(created.getComments());
        assertNull(created.getRequestId());
    }

    @Test
    void create_whenRequestIdIsNotNull_thenReturnItemDtoOut() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User requester = userRepository.save(generator.nextObject(User.class));
        Request request = generator.nextObject(Request.class);
        request.setUser(requester);
        request.setItems(null);
        Request requestFromDb = requestRepository.save(request);
        ItemDtoIn in = generator.nextObject(ItemDtoIn.class);
        in.setRequestId(requestFromDb.getId());
        ItemDtoOut created = itemService.create(in, owner.getId());
        assertNotNull(created.getId());
        assertEquals(in.getName(), created.getName());
        assertEquals(in.getDescription(), created.getDescription());
        assertEquals(in.getAvailable(), created.getAvailable());
        assertNull(created.getLastBooking());
        assertNull(created.getNextBooking());
        assertNull(created.getComments());
        assertEquals(in.getRequestId(), created.getRequestId());
    }

    @Test
    void findById_whenUserIsOwner_returnItemDtoOut() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .build();
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(nextBooking);
        bookingRepository.save(lastBooking);
        ItemDtoOut found = itemService.findById(item.getId(), owner.getId());
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(lastBooking.getId(), found.getLastBooking().getId());
        assertEquals(lastBooking.getUser().getId(), found.getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), found.getNextBooking().getId());
        assertEquals(nextBooking.getUser().getId(), found.getNextBooking().getBookerId());
        assertNull(found.getComments());
    }

    @Test
    void findById_whenUserIsNotOwner_returnItemDtoOut() {
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .build();
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(nextBooking);
        bookingRepository.save(lastBooking);
        ItemDtoOut found = itemService.findById(item.getId(), booker.getId());
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertNull(found.getLastBooking());
        assertNull(found.getLastBooking());
        assertNull(found.getNextBooking());
        assertNull(found.getNextBooking());
        assertNull(found.getComments());
    }

    @Test
    void findAllByUserId_whenCorrectRequest_thenReturnListItemDtoOut() {
        Integer from = 1;
        Integer size = 10;
        User owner = userRepository.save(generator.nextObject(User.class));
        User booker = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        Booking nextBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .build());
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(nextBooking);
        bookingRepository.save(lastBooking);
        List<ItemDtoOut> foundItems = itemService.findAllByUserId(from, size, owner.getId());
        assertThat(foundItems).hasSize(1);
        ItemDtoOut found = foundItems.get(0);
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
        assertEquals(lastBooking.getId(), found.getLastBooking().getId());
        assertEquals(lastBooking.getUser().getId(), found.getLastBooking().getBookerId());
        assertEquals(nextBooking.getId(), found.getNextBooking().getId());
        assertEquals(nextBooking.getUser().getId(), found.getNextBooking().getBookerId());
        assertNull(found.getComments());
    }

    @Test
    void searchByName() {
        int from = 0;
        int size = 10;
        User owner = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        List<ItemDtoOut> foundItems = itemService.searchByNameOrDescription(from, size, item.getName());
        assertThat(foundItems).hasSize(1);
        ItemDtoOut found = foundItems.get(0);
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
    }

    @Test
    void searchByDescription() {
        int from = 0;
        int size = 10;
        User owner = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        item.setRequest(null);
        item.setComments(null);
        item = itemRepository.save(item);
        List<ItemDtoOut> foundItems = itemService.searchByNameOrDescription(from, size, item.getDescription());
        assertThat(foundItems).hasSize(1);
        ItemDtoOut found = foundItems.get(0);
        assertEquals(item.getId(), found.getId());
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());
        assertEquals(item.getAvailable(), found.getAvailable());
    }

    @Test
    void createComment_whenChecksIsOk_thenReturnCommentDtoOut() {
        User commentator = userRepository.save(generator.nextObject(User.class));
        User owner = userRepository.save(generator.nextObject(User.class));
        Item item = generator.nextObject(Item.class);
        item.setUser(owner);
        item.setRequest(null);
        item.setComments(List.of());
        item = itemRepository.save(item);
        Item finalItem = item;
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .user(commentator)
                .status(Status.APPROVED)
                .build());
        CommentDtoOut created = itemService.createComment(finalItem.getId(), commentator.getId(), generator.nextObject(CommentDtoIn.class));
        assertNotNull(created.getId());
        assertEquals(commentator.getName(), created.getAuthorName());
        assertNotNull(created.getText());
        assertNotNull(created.getCreated());
    }

}
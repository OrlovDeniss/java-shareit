package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RequestServiceIntegrationTest {

    @Autowired
    private RequestServiceImpl requestService;
    @Autowired
    private UserRepository userRepository;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void create_whenUserExists_thanCreateAndReturnRequestDtoOut() {
        User owner = userRepository.save(generator.nextObject(User.class));
        RequestDtoIn requestDtoIn = generator.nextObject(RequestDtoIn.class);
        RequestDtoOut created = requestService.create(requestDtoIn, owner.getId());
        assertNotNull(created.getId());
        assertEquals(requestDtoIn.getDescription(), created.getDescription());
        assertNotNull(created.getCreated());
        assertNull(created.getItems());
    }

    @Test
    void findAllByOwnerId_whenUserFound_thanReturnListRequests() {
        User owner = userRepository.save(generator.nextObject(User.class));
        RequestDtoIn requestDtoIn = generator.nextObject(RequestDtoIn.class);
        RequestDtoOut created = requestService.create(requestDtoIn, owner.getId());
        List<RequestDtoOut> requestDtoOut = requestService.findAllByOwnerId(owner.getId());
        assertThat(requestDtoOut).hasSize(1);
        assertEquals(created, requestDtoOut.get(0));
    }

    @Test
    void findAllByUserId_whenUserFound_thanReturnListRequests() {
        int from = 0;
        int size = 10;
        User owner = userRepository.save(generator.nextObject(User.class));
        User anotherUser = userRepository.save(generator.nextObject(User.class));
        RequestDtoIn requestDtoIn = generator.nextObject(RequestDtoIn.class);
        RequestDtoOut created = requestService.create(requestDtoIn, owner.getId());
        List<RequestDtoOut> requestDtoOut = requestService.findAllByUserId(from, size, anotherUser.getId());
        assertThat(requestDtoOut).hasSize(1);
        assertEquals(created, requestDtoOut.get(0));
    }

}
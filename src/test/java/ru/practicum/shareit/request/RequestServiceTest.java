package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.request.RequestNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.pagerequest.PageRequester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    private RequestServiceImpl requestService;
    private final EasyRandom generator = new EasyRandom();

    private final int from = 0;
    private final int size = 10;

    @BeforeEach
    void setUp() {
        requestService = Mockito.mock(RequestServiceImpl.class, CALLS_REAL_METHODS);
    }

    @Test
    void create_whenUserNotExists_thanThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> requestService.create(generator.nextObject(RequestDtoIn.class), generator.nextLong()));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void create_whenUserExists_thanReturnRequestDtoOut() {
        ReflectionTestUtils.setField(requestService, "mapper", RequestMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        User user = generator.nextObject(User.class);
        RequestDtoIn in = generator.nextObject(RequestDtoIn.class);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any(Request.class)))
                .then(returnsFirstArg());
        RequestDtoOut created = requestService.create(in, user.getId());
        assertNotNull(created.getId());
        assertEquals(in.getDescription(), created.getDescription());
        assertNotNull(created.getCreated());
        assertNull(created.getItems());
        verify(requestRepository, times(1))
                .save(any(Request.class));
    }

    @Test
    void findById_whenUserNotExists_thenThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> requestService.findById(generator.nextLong(), generator.nextLong()));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void findById_whenRequestNotExists_thenThrowRequestNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(RequestNotFoundException.class,
                () -> requestService.findById(generator.nextLong(), generator.nextLong()));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void findById_whenCorrect_thenReturnRequestDtoOut() {
        ReflectionTestUtils.setField(requestService, "mapper", RequestMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        Request request = generator.nextObject(Request.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        RequestDtoOut found = requestService.findById(generator.nextLong(), generator.nextLong());
        assertEquals(request.getId(), found.getId());
        assertEquals(request.getDescription(), found.getDescription());
        assertEquals(request.getCreated(), found.getCreated());
        assertNotNull(found.getItems());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void findAllByUserId_whenUserNotFound_thanThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> requestService.findAllByUserId(from, size, generator.nextLong()));
        verify(requestRepository, never()).findAllOtherRequestsByUserId(anyLong(), any(PageRequester.class));
    }

    @Test
    void findAllByUserId_whenUserFound_thanReturnListRequests() {
        ReflectionTestUtils.setField(requestService, "mapper", RequestMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        Request request = generator.nextObject(Request.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findAllOtherRequestsByUserId(anyLong(), any(PageRequester.class)))
                .thenReturn(new PageImpl<>(List.of(request)));
        List<RequestDtoOut> foundList = requestService.findAllByUserId(from, size, generator.nextLong());
        assertThat(foundList).hasSize(1);
        RequestDtoOut found = foundList.get(0);
        assertEquals(request.getId(), found.getId());
        assertEquals(request.getDescription(), found.getDescription());
        assertEquals(request.getCreated(), found.getCreated());
        assertNotNull(found.getItems());
        verify(requestRepository, times(1))
                .findAllOtherRequestsByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    void findAllByOwnerId_whenUserNotFound_thanThrowUserNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> requestService.findAllByOwnerId(generator.nextLong()));
        verify(requestRepository, never()).findAllByOwnerIdWithItems(anyLong());
    }

    @Test
    void findAllByOwnerId_whenUserFound_thanReturnListRequests() {
        ReflectionTestUtils.setField(requestService, "mapper", RequestMapper.INSTANCE);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ReflectionTestUtils.setField(requestService, "userRepository", userRepository);
        RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
        ReflectionTestUtils.setField(requestService, "repository", requestRepository);
        Request request = generator.nextObject(Request.class);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findAllByOwnerIdWithItems(anyLong()))
                .thenReturn(List.of(request));
        List<RequestDtoOut> foundList = requestService.findAllByOwnerId(generator.nextLong());
        assertThat(foundList).hasSize(1);
        RequestDtoOut found = foundList.get(0);
        assertEquals(request.getId(), found.getId());
        assertEquals(request.getDescription(), found.getDescription());
        assertEquals(request.getCreated(), found.getCreated());
        assertNotNull(found.getItems());
        verify(requestRepository, times(1))
                .findAllByOwnerIdWithItems(anyLong());
    }

}
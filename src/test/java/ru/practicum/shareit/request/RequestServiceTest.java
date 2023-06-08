package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RequestServiceTest {

    @Autowired
    RequestServiceImpl requestService;
    @MockBean
    RequestRepository requestRepository;
    @MockBean
    UserRepository userRepository;

    private static final LocalDateTime now = LocalDateTime.now();
    private static final String TEST_DESCRIPTION = "description";

    final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@user.com")
            .build();
    final Request requestFromDb = Request.builder()
            .id(1L)
            .description(TEST_DESCRIPTION)
            .created(now)
            .user(user1)
            .build();

    final Request referenceRequestToDb = Request.builder()
            .description(TEST_DESCRIPTION)
            .created(now)
            .user(user1)
            .build();
    final RequestDtoIn requestDtoIn = RequestDtoIn.builder()
            .description(TEST_DESCRIPTION)
            .build();
    final RequestDtoOut referenceDtoOut = RequestDtoOut.builder()
            .id(1L)
            .description(TEST_DESCRIPTION)
            .created(now)
            .items(Collections.emptyList())
            .build();

    @Captor
    ArgumentCaptor<Request> requestCaptor;

    @Test
    void create_whenUserExists_thanCreateAndReturnRequestDtoOut() {
        when(userRepository.existsById(1L))
                .thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        when(requestRepository.save(any(Request.class)))
                .thenReturn(requestFromDb);

        RequestDtoOut createdRequestDtoOut = requestService.create(requestDtoIn, 1L);
        assertNotNull(createdRequestDtoOut);
        assertNotNull(createdRequestDtoOut.getCreated());
        assertEquals(createdRequestDtoOut, referenceDtoOut);

        verify(requestRepository)
                .save(requestCaptor.capture());
        Request requestToDbCaptor = requestCaptor.getValue();
        assertNotNull(requestToDbCaptor.getCreated());
        requestToDbCaptor.setCreated(now);
        assertEquals(requestToDbCaptor, referenceRequestToDb);
    }

    @Test
    void create_whenUserNotExists_thanThrowUserNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> requestService.create(requestDtoIn, userId));

        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void findAllByOwnerId_whenUserNotFound_thanThrowUserNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> requestService.findAllByOwnerId(userId));

        verify(requestRepository, never()).findAllByOwnerIdWithItems(anyLong());
    }

    @Test
    void findAllByOwnerId_whenUserFound_thanReturnListRequests() {
        Long userId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(requestRepository.findAllByOwnerIdWithItems(userId))
                .thenReturn(List.of(requestFromDb));

        List<RequestDtoOut> requestDtoOut = requestService.findAllByOwnerId(userId);
        assertThat(requestDtoOut).hasSize(1);
        assertEquals(requestDtoOut.get(0), referenceDtoOut);

        verify(requestRepository, times(1))
                .findAllByOwnerIdWithItems(anyLong());
    }

    @Test
    void findAllByUserId_whenUserFound_thanReturnListRequests() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(requestRepository.findAllOtherRequestsByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(requestFromDb)));

        List<RequestDtoOut> requestDtoOut = requestService.findAllByUserId(from, size, userId);
        assertThat(requestDtoOut).hasSize(1);
        assertEquals(requestDtoOut.get(0), referenceDtoOut);

        verify(requestRepository, times(1)).findAllOtherRequestsByUserId(anyLong(), any(PageRequest.class));
    }

    @Test
    void findAllByUserId_whenUserNotFound_thanThrowUserNotFoundException() {
        Integer from = 1;
        Integer size = 1;
        Long userId = 1L;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        when(userRepository.existsById(userId))
                .thenReturn(false);
        when(requestRepository.findAllOtherRequestsByUserId(userId, pageable))
                .thenReturn(new PageImpl<>(List.of(requestFromDb)));

        assertThrows(UserNotFoundException.class,
                () -> requestService.findAllByOwnerId(userId));

        verify(requestRepository, never()).findAllOtherRequestsByUserId(anyLong(), any(PageRequest.class));
    }

}
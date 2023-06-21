package ru.practicum.shareit.request.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.request.RequestNotFoundException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.pagerequest.PageRequester;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RequestServiceImpl extends AbstractService<RequestDtoIn, RequestDtoOut, Request> implements RequestService {

    private final UserRepository userRepository;

    public RequestServiceImpl(JpaRepository<Request, Long> repository,
                              ObjectMapper objectMapper,
                              UserRepository userRepository) {
        super(repository, RequestMapper.INSTANCE, objectMapper);
        this.userRepository = userRepository;
    }

    @Override
    public RequestDtoOut create(RequestDtoIn requestDtoIn, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Request request = toEntity(requestDtoIn);
        request.setCreated(LocalDateTime.now());
        request.setUser(user);
        return toDto(getRepository().save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDtoOut findById(Long requestId, Long userId) {
        throwWhenUserNotExist(userId);
        return toDto(getRepository().findById(requestId).orElseThrow(RequestNotFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoOut> findAllByOwnerId(Long userId) {
        throwWhenUserNotExist(userId);
        return toDto(getRepository().findAllByOwnerIdWithItems(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoOut> findAllByUserId(Integer from, Integer size, Long userId) {
        throwWhenUserNotExist(userId);
        Pageable sortedByCreatedDesc = new PageRequester(from, size, Sort.by("created").descending());
        return toDto(getRepository().findAllOtherRequestsByUserId(userId, sortedByCreatedDesc).toList());
    }

    private void throwWhenUserNotExist(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("id = %d не существует!", id));
        }
    }

    private RequestRepository getRepository() {
        return (RequestRepository) repository;
    }

}
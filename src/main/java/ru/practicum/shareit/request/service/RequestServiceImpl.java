package ru.practicum.shareit.request.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.userobject.AbstractUserObjectService;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestModelMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestServiceImpl extends AbstractUserObjectService<RequestDtoIn, RequestDtoOut, Request>
        implements RequestService {

    private final RequestRepository requestRepository;

    protected RequestServiceImpl(RequestModelMapper mapper,
                                 UserRepository userRepository,
                                 ObjectMapper objectMapper,
                                 RequestRepository requestRepository) {
        super(mapper, userRepository, objectMapper, requestRepository);
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional
    public RequestDtoOut create(RequestDtoIn requestDtoIn, Long userId) {
        throwWhenUserNotFound(userId);
        Request request = toEntity(requestDtoIn);
        request.setCreated(LocalDateTime.now());
        return toDto(createUserObject(request, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoOut> findAllByOwnerId(Long userId) {
        throwWhenUserNotFound(userId);
        return toDto(requestRepository.findAllByOwnerIdWithItems(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDtoOut> findAllByUserId(Integer from, Integer size, Long userId) {
        throwWhenUserNotFound(userId);
        Pageable sortedByCreatedDesc = PageRequest.of(from / size, size, Sort.by("created").descending());
        return toDto(requestRepository.findAllOtherRequestsByUserId(userId, sortedByCreatedDesc).toList());
    }

}
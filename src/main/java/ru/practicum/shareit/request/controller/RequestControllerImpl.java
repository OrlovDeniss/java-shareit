package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.abstraction.controller.AbstractController;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestControllerImpl extends AbstractController implements RequestController {

    private final RequestService requestService;

    @Override
    public RequestDtoOut get(Long requestId, Long userId) {
        return requestService.findById(requestId, userId);
    }

    @Override
    public RequestDtoOut post(RequestDtoIn requestDtoIn, Long userId) {
        return requestService.create(requestDtoIn, userId);
    }

    @Override
    public List<RequestDtoOut> getAllCreatedByOtherUsers(Integer from, Integer size, Long userId) {
        if (Objects.isNull(from) || Objects.isNull(size)) {
            return Collections.emptyList();
        }
        throwWhenFromLessThenZero(from);
        throwWhenSizeLessThanOne(size);
        return requestService.findAllByUserId(from, size, userId);
    }

    @Override
    public List<RequestDtoOut> getAllCreatedByUser(Long ownerId) {
        return requestService.findAllByOwnerId(ownerId);
    }

}
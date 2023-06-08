package ru.practicum.shareit.abstraction.userobject;

import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;

class RequestAbstractUserObjectServiceTest extends AbstractUserObjectServiceTest<RequestDtoIn, RequestDtoOut, Request> {

    private static final Long REQUEST_ID = 1L;
    private static final String REQUEST_DESCRIPTION = "description";
    private static final LocalDateTime REQUEST_CREATED = LocalDateTime.of(2025, 1, 1, 1, 1);

    @Override
    protected Request getEntity() {
        return Request.builder()
                .id(REQUEST_ID)
                .description(REQUEST_DESCRIPTION)
                .created(REQUEST_CREATED)
                .build();
    }

    @Override
    protected RequestDtoIn getDtoIn() {
        return RequestDtoIn.builder()
                .id(REQUEST_ID)
                .description(REQUEST_DESCRIPTION)
                .build();
    }

    @Override
    protected RequestDtoOut getDtoOut() {
        return RequestDtoOut.builder()
                .id(REQUEST_ID)
                .description(REQUEST_DESCRIPTION)
                .created(REQUEST_CREATED)
                .build();
    }

}

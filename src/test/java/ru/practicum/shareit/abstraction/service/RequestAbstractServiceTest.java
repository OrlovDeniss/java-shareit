package ru.practicum.shareit.abstraction.service;

import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.Map;

class RequestAbstractServiceTest extends AbstractServiceTest<Request> {

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
    protected Request getReference() {
        return Request.builder()
                .id(REQUEST_ID)
                .description(REQUEST_DESCRIPTION)
                .created(REQUEST_CREATED)
                .build();
    }

    @Override
    protected Map<String, Object> getNewFields() {
        return Map.of();
    }

}
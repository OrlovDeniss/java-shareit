package ru.practicum.shareit.abstraction.userobject.entityservice;

import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;

public class RequestAbstractUserObjectEntityServiceTest extends AbstractUserObjectEntityServiceTest<Request> {

    @Override
    protected Request getEntity() {
        return Request.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.of(2004,1,1,1,1,1))
                .build();
    }

}
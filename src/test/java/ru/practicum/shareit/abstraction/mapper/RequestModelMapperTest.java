package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestModelMapperTest extends AbstractModelMapperTest<RequestDtoIn, RequestDtoOut, Request> {

    private static final Long ITEM_ID = 2L;
    private static final Long USER_ID = 3L;
    private static final Long REQUEST_ID = 5L;
    private static final LocalDateTime REQUEST_CREATED = LocalDateTime.of(2222, 1, 1, 1, 1, 1);
    private static final String ITEM_NAME = "item_name";
    private static final String ITEM_DESCRIPTION = "item_description";
    private static final String REQUEST_DESCRIPTION = "request_description";
    private static final boolean AVAILABLE = true;

    final User user = User.builder()
            .id(USER_ID)
            .name("user")
            .email("user@user.com")
            .build();

    final Item item = Item.builder()
            .id(ITEM_ID)
            .name(ITEM_NAME)
            .description(ITEM_DESCRIPTION)
            .available(AVAILABLE)
            .user(user)
            .build();

    final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(ITEM_ID)
            .name(ITEM_NAME)
            .description(ITEM_DESCRIPTION)
            .available(AVAILABLE)
            .ownerId(USER_ID)
            .build();

    final Request request = Request.builder()
            .id(REQUEST_ID)
            .description(REQUEST_DESCRIPTION)
            .created(REQUEST_CREATED)
            .items(List.of(item))
            .user(user)
            .build();

    final RequestDtoIn requestDtoIn = RequestDtoIn.builder()
            .id(REQUEST_ID)
            .description(REQUEST_DESCRIPTION)
            .build();

    final RequestDtoOut requestDtoOut = RequestDtoOut.builder()
            .id(REQUEST_ID)
            .description(REQUEST_DESCRIPTION)
            .created(REQUEST_CREATED)
            .items(List.of(itemDtoShort))
            .build();

    @Override
    protected Request getEntity() {
        return request;
    }

    @Override
    protected RequestDtoIn getDtoIn() {
        return requestDtoIn;
    }

    @Override
    protected RequestDtoOut getDtoOut() {
        return requestDtoOut;
    }

    @Test
    @Override
    void toEntityTest() {
        Request request1 = mapper.toEntity(getDtoIn());
        request1.setCreated(REQUEST_CREATED); // service layer responsibility
        assertEquals(getEntity(), request1);
    }
}
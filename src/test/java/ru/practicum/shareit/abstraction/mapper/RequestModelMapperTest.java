package ru.practicum.shareit.abstraction.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestModelMapperTest extends AbstractModelMapperTest<RequestDtoIn, RequestDtoOut, Request> {

    private final Long userId = generator.nextLong();
    private final String userName = generator.nextObject(String.class);
    private final String userEmail = generator.nextObject(String.class);

    private final Long itemId = generator.nextLong();
    private final String itemName = generator.nextObject(String.class);
    private final String itemDescription = generator.nextObject(String.class);
    private final Boolean itemAvailable = generator.nextBoolean();

    private final Long requestId = generator.nextLong();
    private final LocalDateTime requestCreated = generator.nextObject(LocalDateTime.class);
    private final String requestDescription = generator.nextObject(String.class);

    private final User user = User.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
            .build();

    private final Item item = Item.builder()
            .id(itemId)
            .name(itemName)
            .description(itemDescription)
            .available(itemAvailable)
            .user(user)
            .request(Request.builder().id(requestId).build())
            .build();

    private final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(itemId)
            .name(itemName)
            .description(itemDescription)
            .available(itemAvailable)
            .requestId(requestId)
            .ownerId(userId)
            .build();

    private final Request request = Request.builder()
            .id(requestId)
            .description(requestDescription)
            .created(requestCreated)
            .items(List.of(item))
            .user(user)
            .build();

    private final RequestDtoIn requestDtoIn = RequestDtoIn.builder()
            .id(requestId)
            .description(requestDescription)
            .build();

    private final RequestDtoOut requestDtoOut = RequestDtoOut.builder()
            .id(requestId)
            .description(requestDescription)
            .created(requestCreated)
            .items(List.of(itemDtoShort))
            .build();

    protected RequestModelMapperTest() {
        super(RequestMapper.INSTANCE);
    }

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
        Request r = mapper.toEntity(getDtoIn());
        r.setCreated(requestCreated); // service layer responsibility
        assertEquals(getEntity(), r);
    }

}
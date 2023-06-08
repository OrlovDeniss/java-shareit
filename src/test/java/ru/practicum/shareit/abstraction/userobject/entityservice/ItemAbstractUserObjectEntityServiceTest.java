package ru.practicum.shareit.abstraction.userobject.entityservice;

import ru.practicum.shareit.item.model.Item;

public class ItemAbstractUserObjectEntityServiceTest extends AbstractUserObjectEntityServiceTest<Item> {

    @Override
    protected Item getEntity() {
        return Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
    }

}
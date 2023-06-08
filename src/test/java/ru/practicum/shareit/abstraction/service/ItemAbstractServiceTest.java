package ru.practicum.shareit.abstraction.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;

class ItemAbstractServiceTest extends AbstractServiceTest<Item> {

    private static final Long ITEM_ID = 1L;
    private static final String ITEM_NAME = "name";
    private static final String UPDATED_ITEM_NAME = "updName";
    private static final String ITEM_DESCRIPTION = "description";
    private static final String UPDATED_ITEM_DESCRIPTION = "updDescription";

    @Override
    protected Item getEntity() {
        return Item.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .build();
    }

    @Override
    protected Item getReference() {
        return Item.builder()
                .id(ITEM_ID)
                .name(UPDATED_ITEM_NAME)
                .description(UPDATED_ITEM_DESCRIPTION)
                .build();
    }

    @Override
    protected Map<String, Object> getNewFields() {
        return Map.of(
                "name", UPDATED_ITEM_NAME,
                "description", UPDATED_ITEM_DESCRIPTION
        );
    }

}

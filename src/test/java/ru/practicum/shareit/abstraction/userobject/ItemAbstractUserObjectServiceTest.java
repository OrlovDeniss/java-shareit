package ru.practicum.shareit.abstraction.userobject;

import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;

class ItemAbstractUserObjectServiceTest extends AbstractUserObjectServiceTest<ItemDtoIn, ItemDtoOut, Item> {

    private static final Long ITEM_ID = 1L;
    private static final String ITEM_NAME = "name";
    private static final String ITEM_DESCRIPTION = "description";
    private static final boolean ITEM_AVAILABLE = true;

    @Override
    protected Item getEntity() {
        return Item.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .available(ITEM_AVAILABLE)
                .build();
    }

    @Override
    protected ItemDtoIn getDtoIn() {
        return ItemDtoIn.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .available(ITEM_AVAILABLE)
                .build();
    }

    @Override
    protected ItemDtoOut getDtoOut() {
        return ItemDtoOut.builder()
                .id(ITEM_ID)
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .available(ITEM_AVAILABLE)
                .build();
    }

}

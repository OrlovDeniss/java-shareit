package ru.practicum.shareit.abstraction.mapper;

import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;

class UserModelMapperTest extends AbstractModelMapperTest<ItemDtoIn, ItemDtoOut, Item> {

    private static final Long ID = 1L;
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final boolean AVAILABLE = true;

    final Item item = Item.builder()
            .id(ID)
            .name(NAME)
            .description(DESCRIPTION)
            .available(AVAILABLE)
            .build();

    final ItemDtoIn itemDtoIn = ItemDtoIn.builder()
            .id(ID)
            .name(NAME)
            .description(DESCRIPTION)
            .available(AVAILABLE)
            .build();

    final ItemDtoOut itemDtoOut = ItemDtoOut.builder()
            .id(ID)
            .name(NAME)
            .description(DESCRIPTION)
            .available(AVAILABLE)
            .build();

    @Override
    protected Item getEntity() {
        return item;
    }

    @Override
    protected ItemDtoIn getDtoIn() {
        return itemDtoIn;
    }

    @Override
    protected ItemDtoOut getDtoOut() {
        return itemDtoOut;
    }
}

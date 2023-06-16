package ru.practicum.shareit.abstraction.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;

class ItemAbstractServiceTest extends AbstractServiceTest<Item> {

    private final Long itemId = generator.nextLong();
    private final String itemName = generator.nextObject(String.class);
    private final String itemDescription = generator.nextObject(String.class);

    private final String updatedItemName = generator.nextObject(String.class);
    private final String updatedItemDescription = generator.nextObject(String.class);

    @Override
    protected Item getEntity() {
        return Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .build();
    }

    @Override
    protected Item getReference() {
        return Item.builder()
                .id(itemId)
                .name(updatedItemName)
                .description(updatedItemDescription)
                .build();
    }

    @Override
    protected Map<String, Object> getNewFields() {
        return Map.of(
                "name", updatedItemName,
                "description", updatedItemDescription
        );
    }

}

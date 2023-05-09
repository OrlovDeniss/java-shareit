package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Item implements UserObject {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User user;
    private ItemRequest request;
}
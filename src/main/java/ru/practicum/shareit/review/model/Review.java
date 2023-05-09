package ru.practicum.shareit.review.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Review implements UserObject {
    private Long id;
    private String description;
    private User user;
    private Item item;
}
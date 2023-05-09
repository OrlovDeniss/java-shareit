package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest implements UserObject {
    private Long id;
    private String description;
    private User user; // requestor
    private LocalDateTime created;
}

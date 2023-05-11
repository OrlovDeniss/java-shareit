package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.abstraction.model.Entity;

@Data
@Builder
public class User implements Entity {
    private Long id;
    private String name;
    private String email;
}
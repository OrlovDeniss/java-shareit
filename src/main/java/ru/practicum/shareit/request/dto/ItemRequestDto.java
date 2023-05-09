package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.abstraction.model.Entity;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto implements Entity {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}

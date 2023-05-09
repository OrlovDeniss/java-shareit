package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.abstraction.model.UserObject;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking implements UserObject {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User user; // booker
    private Status status;
}
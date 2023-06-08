package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoOut implements Identified {

    @NonNull
    private Long id;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    @NonNull
    private ItemDtoShort item;
    @NonNull
    private UserDtoShort booker;
    @NonNull
    private Status status;

}
package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.abstraction.model.Identified;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDtoOut implements Identified {

    @NonNull
    private Long id;

    @NonNull
    private String description;

    @NonNull
    private LocalDateTime created;

    private List<ItemDtoShort> items;

}
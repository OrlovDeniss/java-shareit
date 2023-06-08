package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.abstraction.model.Identified;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoShort implements Identified {

    @NonNull
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    private boolean available;

    private Long requestId;

    @NonNull
    private Long ownerId;

}
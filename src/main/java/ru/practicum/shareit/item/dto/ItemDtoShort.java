package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.abstraction.model.Identified;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoShort implements Identified {

    private Long id;
    private String name;

}
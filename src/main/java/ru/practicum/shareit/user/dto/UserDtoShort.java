package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.abstraction.model.Identified;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoShort implements Identified {

    private Long id;

}
package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.abstraction.model.Identified;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoShort implements Identified {

    @NonNull
    private Long id;

}
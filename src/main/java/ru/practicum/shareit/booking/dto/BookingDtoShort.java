package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.abstraction.model.Identified;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoShort implements Identified {

    @NonNull
    private Long id;
    @NonNull
    private Long bookerId;

}
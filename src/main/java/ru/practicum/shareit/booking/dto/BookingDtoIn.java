package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoIn {

    @Positive
    private Long id;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private LocalDateTime start;

    @NotNull
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private LocalDateTime end;

    @NotNull
    @Positive
    private Long itemId;

}
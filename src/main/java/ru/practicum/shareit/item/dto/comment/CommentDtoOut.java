package ru.practicum.shareit.item.dto.comment;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoOut {

    @NonNull
    private Long id;
    @NonNull
    private String text;
    @NonNull
    private String authorName;
    @NonNull
    private LocalDateTime created;

}
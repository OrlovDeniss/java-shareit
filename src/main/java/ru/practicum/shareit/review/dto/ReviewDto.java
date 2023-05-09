package ru.practicum.shareit.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.abstraction.model.Entity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ReviewDto implements Entity {
    @JsonProperty("id")
    private Long id;
    @NotBlank
    @JsonProperty("description")
    private String description;
    private User user;
    private Item item;
}
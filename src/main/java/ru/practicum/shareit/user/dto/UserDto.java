package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.abstraction.model.Entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto implements Entity {

    @JsonProperty("id")
    private Long id;

    @NotBlank
    @JsonProperty("name")
    private String name;

    @Email
    @NotNull
    @JsonProperty("email")
    private String email;

}
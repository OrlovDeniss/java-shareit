package ru.practicum.shareit.user.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

public interface UserController {

    @GetMapping("{id}")
    UserDto get(@PathVariable @Positive Long id);

    @PostMapping
    UserDto post(@Valid @RequestBody UserDto userDto);

    @PutMapping
    UserDto put(@Valid @RequestBody UserDto userDto);

    @PatchMapping("{id}")
    UserDto patch(@PathVariable @Positive Long id,
                  @RequestBody Map<String, Object> newFields);

    @GetMapping
    List<UserDto> getAll();

    @DeleteMapping("/{id}")
    void delete(@PathVariable @Positive Long id);

}
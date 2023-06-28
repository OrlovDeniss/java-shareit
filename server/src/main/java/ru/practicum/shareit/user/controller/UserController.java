package ru.practicum.shareit.user.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserDto;

import java.util.List;
import java.util.Map;

public interface UserController {

    @GetMapping("{id}")
    UserDto get(@PathVariable("id") Long userId);

    @PostMapping
    UserDto post(@RequestBody UserDto userDto);

    @PutMapping
    UserDto put(@RequestBody UserDto userDto);

    @PatchMapping("{id}")
    UserDto patch(@PathVariable("id") Long userId,
                  @RequestBody Map<String, Object> newFields);

    @GetMapping
    List<UserDto> getAll();

    @DeleteMapping("/{id}")
    void delete(@PathVariable("id") Long userId);

}
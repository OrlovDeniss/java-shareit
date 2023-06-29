package ru.practicum.shareit.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Map;

public interface UserController {

    @GetMapping("{id}")
    ResponseEntity<Object> get(@PathVariable("id") @Positive Long userId);

    @PostMapping
    ResponseEntity<Object> post(@Valid @RequestBody UserDto userDto);

    @PutMapping
    ResponseEntity<Object> put(@Valid @RequestBody UserDto userDto);

    @PatchMapping("{id}")
    ResponseEntity<Object> patch(@PathVariable("id") @Positive Long userId,
                                 @RequestBody Map<String, Object> fields);

    @GetMapping
    ResponseEntity<Object> getAll();

    @DeleteMapping("/{id}")
    void delete(@PathVariable("id") @Positive Long userId);

}
package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserControllerImpl {

    protected final UserService service;

    @GetMapping("{id}")
    public UserDto get(@PathVariable @Positive Long id) {
        return service.findById(id);
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return service.create(userDto);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UserDto userDto) {
        return service.update(userDto);
    }

    @PatchMapping("{id}")
    public UserDto patch(@PathVariable @Positive Long id,
                         @RequestBody Map<String, Object> newFields) {
        return service.patch(id, newFields);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return service.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        service.delete(id);
    }
}
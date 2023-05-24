package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserControllerImpl implements UserController {

    private final UserService service;

    public UserDto get(Long id) {
        return service.findById(id);
    }

    public UserDto add(UserDto userDto) {
        return service.create(userDto);
    }

    public UserDto update(UserDto userDto) {
        return service.update(userDto);
    }

    public UserDto patch(Long id, Map<String, Object> newFields) {
        return service.patch(id, newFields);
    }

    public List<UserDto> findAll() {
        return service.findAll();
    }

    public void delete(Long id) {
        service.delete(id);
    }

}
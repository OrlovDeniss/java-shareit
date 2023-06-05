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

    private final UserService userService;

    public UserDto get(Long id) {
        return userService.findById(id);
    }

    public UserDto post(UserDto userDto) {
        return userService.create(userDto);
    }

    public UserDto put(UserDto userDto) {
        return userService.update(userDto);
    }

    public UserDto patch(Long id, Map<String, Object> newFields) {
        return userService.patch(id, newFields);
    }

    public List<UserDto> getAll() {
        return userService.findAll();
    }

    public void delete(Long id) {
        userService.delete(id);
    }

}
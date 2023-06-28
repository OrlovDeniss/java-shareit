package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.general.JsonUpdateFieldsException;
import ru.practicum.shareit.util.exception.user.UserEmailAlreadyExistsException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public UserDto create(UserDto userDto) {
        return toDto(userRepository.save(toEntity(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto) {
        throwWhenUserNotExist(userDto.getId());
        return toDto(userRepository.save(toEntity(userDto)));
    }

    @Override
    public UserDto patch(Long userId, Map<String, Object> newFields) {
        throwWhenEmailAlreadyExists(userId, newFields);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return toDto(userRepository.save(tryUpdateFields(user, newFields)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return toDto(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return toDto(userRepository.findAll());
    }

    @Override
    public void delete(Long id) {
        throwWhenUserNotExist(id);
        userRepository.deleteById(id);
    }

    private User tryUpdateFields(User user, Map<String, Object> newFields) {
        try {
            return objectMapper.updateValue(user, newFields);
        } catch (JsonMappingException e) {
            throw new JsonUpdateFieldsException(
                    String.format("Невозможно обновить поля объекта: %s", user.getClass().getSimpleName()));
        }
    }

    private void throwWhenUserNotExist(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("id = %d не существует!", id));
        }
    }

    private void throwWhenEmailAlreadyExists(Long userId, Map<String, Object> newFields) {
        String newEmail = String.valueOf(newFields.get("email"));
        if (userRepository.existsByEmailAndIdNot(newEmail, userId)) {
            throw new UserEmailAlreadyExistsException(String.format("Email = %s уже существует!", newEmail));
        }
    }

    private UserDto toDto(User user) {
        return UserMapper.INSTANCE.toDto(user);
    }

    private List<UserDto> toDto(List<User> user) {
        return UserMapper.INSTANCE.toDto(user);
    }

    private User toEntity(UserDto userDto) {
        return UserMapper.INSTANCE.toEntity(userDto);
    }

}
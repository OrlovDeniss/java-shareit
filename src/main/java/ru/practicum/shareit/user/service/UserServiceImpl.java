package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.user.UserEmailAlreadyExistsException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends AbstractService<UserDto, UserDto, User> implements UserService {

    public UserServiceImpl(ModelMapper<UserDto, UserDto, User> mapper,
                           UserRepository userRepository,
                           ObjectMapper objectMapper) {
        super(mapper, userRepository, objectMapper);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return toDto(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

    @Transactional
    public UserDto create(UserDto userDTO) {
        return toDto(userRepository.save(toEntity(userDTO)));
    }

    @Transactional
    public UserDto update(UserDto userDto) {
        throwWhenUserNotFound(userDto.getId());
        return toDto(userRepository.save(toEntity(userDto)));
    }

    @Transactional
    public UserDto patch(Long id, Map<String, Object> newFields) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        throwWhenEmailAlreadyExists(newFields, user);
        return toDto(userRepository.save(tryUpdateFields(user, newFields)));
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return toDto(userRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        throwWhenUserNotFound(id);
        userRepository.deleteById(id);
    }

    private void throwWhenUserNotFound(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("id = %d не существует!", id));
        }
    }

    private void throwWhenEmailAlreadyExists(Map<String, Object> newFields, User user) {
        String newEmail = String.valueOf(newFields.get("email"));
        if (userRepository.existsByEmail(newEmail) && !user.getEmail().equals(newEmail)) {
            throw new UserEmailAlreadyExistsException(String.format("Email = %s уже существует!", newEmail));
        }
    }

}
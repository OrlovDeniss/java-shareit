package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.service.AbstractService;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends AbstractService<UserDto, User> implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(ModelMapper<UserDto, User> mapper,
                           UserRepository repository) {
        super(mapper);
        this.repository = repository;
    }

    public UserDto findById(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public UserDto create(UserDto userDTO) {
        throwIfEmailAlreadyExists(userDTO.getEmail());
        return mapper.toDto(repository.save(mapper.toEntity(userDTO)));
    }

    public UserDto update(UserDto userDto) {
        if (repository.existsById(userDto.getId())) {
            return mapper.toDto(repository.update(mapper.toEntity(userDto)));
        } else {
            throw new EntityNotFoundException(String.format("id = %d не существует!", userDto.getId()));
        }
    }

    public UserDto patch(Long id, Map<String, Object> newFields) {
        UserDto userDTO = findById(id);
        String newEmail = String.valueOf(newFields.get("email"));
        if (userDTO.getEmail().equals(newEmail)) {
            return mapper.toDto(repository.update(mapper.toEntity(tryUpdateFields(userDTO, newFields))));
        } else {
            throwIfEmailAlreadyExists(newEmail);
            return mapper.toDto(repository.update(mapper.toEntity(tryUpdateFields(userDTO, newFields))));
        }
    }

    public List<UserDto> findAll() {
        return mapper.toDto(repository.findAll());
    }

    public void delete(Long id) {
        if (repository.existsById(id)) {
            repository.delete(id);
        } else {
            throw new EntityNotFoundException(String.format("id = %d не существует!", id));
        }
    }

    private void throwIfEmailAlreadyExists(String email) {
        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(String.format("Email = %s уже существует!", email));
        }
    }

}

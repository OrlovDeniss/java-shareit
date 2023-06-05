package ru.practicum.shareit.util.exception.user;

public class UserEmailAlreadyExistsException extends RuntimeException {

    public UserEmailAlreadyExistsException(String message) {
        super(message);
    }
}

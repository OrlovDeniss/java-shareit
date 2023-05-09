package ru.practicum.shareit.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Пользователь не найден.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}

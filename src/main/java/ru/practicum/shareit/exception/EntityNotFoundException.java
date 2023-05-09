package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
        super("Объект не найден.");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}

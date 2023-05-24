package ru.practicum.shareit.util.exception.general;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
        super("Объект не найден.");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}

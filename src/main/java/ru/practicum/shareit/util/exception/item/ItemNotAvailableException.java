package ru.practicum.shareit.util.exception.item;

public class ItemNotAvailableException extends RuntimeException {

    public ItemNotAvailableException() {
        super("Предмет не доступен.");
    }

    public ItemNotAvailableException(String message) {
        super(message);
    }
}

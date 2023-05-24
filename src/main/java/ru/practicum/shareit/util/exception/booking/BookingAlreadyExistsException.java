package ru.practicum.shareit.util.exception.booking;

public class BookingAlreadyExistsException extends RuntimeException {

    public BookingAlreadyExistsException() {
        super("Аренда уже существует.");
    }

    public BookingAlreadyExistsException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.util.exception.booking;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException() {
        super("Аренда не найдена.");
    }

    public BookingNotFoundException(String message) {
        super(message);
    }
}

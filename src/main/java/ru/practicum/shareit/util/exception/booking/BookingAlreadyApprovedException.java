package ru.practicum.shareit.util.exception.booking;

public class BookingAlreadyApprovedException extends RuntimeException {

    public BookingAlreadyApprovedException() {
        super("Аренда уже принята.");
    }

    public BookingAlreadyApprovedException(String message) {
        super(message);
    }
}

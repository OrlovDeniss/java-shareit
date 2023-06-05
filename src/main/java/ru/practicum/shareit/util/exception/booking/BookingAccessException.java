package ru.practicum.shareit.util.exception.booking;

public class BookingAccessException extends RuntimeException {

    public BookingAccessException() {
        super("Доступ к аренде имеет либо автор бронирования, либо владелец вещи.");
    }

    public BookingAccessException(String message) {
        super(message);
    }
}

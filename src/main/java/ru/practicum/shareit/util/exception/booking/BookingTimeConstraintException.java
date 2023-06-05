package ru.practicum.shareit.util.exception.booking;

public class BookingTimeConstraintException extends RuntimeException {

    public BookingTimeConstraintException() {
        super("Время начала аренды должно быть < времени окончания аренды.");
    }

    public BookingTimeConstraintException(String message) {
        super(message);
    }
}

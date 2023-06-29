package ru.practicum.shareit.util.exception;

public class BookingStartEndTimeException extends RuntimeException {

    public BookingStartEndTimeException() {
        super("Время начала аренды должно быть < времени окончания аренды.");
    }

}

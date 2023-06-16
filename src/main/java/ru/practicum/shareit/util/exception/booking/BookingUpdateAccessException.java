package ru.practicum.shareit.util.exception.booking;

public class BookingUpdateAccessException extends RuntimeException {

    public BookingUpdateAccessException() {
        super("Обновить аренду может только автор аренды.");
    }

}

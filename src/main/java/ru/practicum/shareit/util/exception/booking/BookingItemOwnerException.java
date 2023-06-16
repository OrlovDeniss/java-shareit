package ru.practicum.shareit.util.exception.booking;

public class BookingItemOwnerException extends RuntimeException {

    public BookingItemOwnerException() {
        super("Владелец предмета не может получить его в аренду.");
    }

}

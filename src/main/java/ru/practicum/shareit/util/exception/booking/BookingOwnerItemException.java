package ru.practicum.shareit.util.exception.booking;

public class BookingOwnerItemException extends RuntimeException {

    public BookingOwnerItemException() {
        super("Владелец предмета не может получить его в аренду.");
    }

    public BookingOwnerItemException(String message) {
        super(message);
    }
}

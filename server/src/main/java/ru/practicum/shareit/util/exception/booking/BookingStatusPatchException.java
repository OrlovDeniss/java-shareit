package ru.practicum.shareit.util.exception.booking;

public class BookingStatusPatchException extends RuntimeException {

    public BookingStatusPatchException() {
        super("Изменить статус аренды может только владелец вещи.");
    }

}

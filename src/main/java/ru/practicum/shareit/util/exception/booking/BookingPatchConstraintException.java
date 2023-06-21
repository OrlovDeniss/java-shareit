package ru.practicum.shareit.util.exception.booking;

public class BookingPatchConstraintException extends RuntimeException {

    public BookingPatchConstraintException() {
        super("Нет аренды со статусом WAITING или пользователь не владелец предмета.");
    }

}
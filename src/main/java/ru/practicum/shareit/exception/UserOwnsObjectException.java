package ru.practicum.shareit.exception;

public class UserOwnsObjectException extends RuntimeException {

    public UserOwnsObjectException(String message) {
        super(message);
    }
}

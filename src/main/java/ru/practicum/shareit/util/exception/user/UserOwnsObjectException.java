package ru.practicum.shareit.util.exception.user;

public class UserOwnsObjectException extends RuntimeException {

    public UserOwnsObjectException(String message) {
        super(message);
    }
}

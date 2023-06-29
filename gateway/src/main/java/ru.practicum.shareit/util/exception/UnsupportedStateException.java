package ru.practicum.shareit.util.exception;

public class UnsupportedStateException extends RuntimeException {

    public UnsupportedStateException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }

}

package ru.practicum.shareit.util.exception.general;

public class UnsupportedStateException extends RuntimeException {

    public UnsupportedStateException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }

}

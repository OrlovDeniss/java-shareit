package ru.practicum.shareit.util.exception.request;

public class RequestNotFoundException extends RuntimeException {

    public RequestNotFoundException() {
        super("Request не существует.");
    }

}

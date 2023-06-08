package ru.practicum.shareit.util.exception.general;

public class IncorrectRequestParamException extends RuntimeException {

    public IncorrectRequestParamException() {
        super("RequestParam имеет недопустимое значение.");
    }

    public IncorrectRequestParamException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.util.exception.general;

public class MethodNotAllowedException extends RuntimeException {

    public MethodNotAllowedException() {
        super("Метод не разрешен..");
    }

    public MethodNotAllowedException(String message) {
        super(message);
    }
}

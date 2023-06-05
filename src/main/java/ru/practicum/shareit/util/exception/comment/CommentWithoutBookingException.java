package ru.practicum.shareit.util.exception.comment;

public class CommentWithoutBookingException extends RuntimeException {

    public CommentWithoutBookingException() {
        super("Комментарий может добавить пользователь, который брал вещь в аренду.");
    }

    public CommentWithoutBookingException(String message) {
        super(message);
    }
}
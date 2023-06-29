package ru.practicum.shareit.util.exception.comment;

public class CommentOwnItemException extends RuntimeException {

    public CommentOwnItemException() {
        super("Владелец не может добавить отзыв к своему предмету.");
    }

}
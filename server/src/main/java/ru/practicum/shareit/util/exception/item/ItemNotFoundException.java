package ru.practicum.shareit.util.exception.item;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException() {
        super("Предмет не найден.");
    }

}
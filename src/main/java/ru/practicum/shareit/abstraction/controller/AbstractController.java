package ru.practicum.shareit.abstraction.controller;

import ru.practicum.shareit.util.exception.general.IncorrectRequestParamException;

public abstract class AbstractController {

    protected void throwWhenSizeLessThanOne(Integer size) {
        if (size < 1) {
            throw new IncorrectRequestParamException("RequestParam size должен быть >= 1.");
        }
    }

    protected void throwWhenFromLessThenZero(Integer from) {
        if (from < 0) {
            throw new IncorrectRequestParamException("RequestParam from должен быть >= 0.");
        }
    }

}
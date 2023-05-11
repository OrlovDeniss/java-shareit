package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleModelFields(final MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        log.info(errors.toString());
        return new ApiError(errors.toString());
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            UserOwnsObjectException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleBadRequest(final RuntimeException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler({
            JsonUpdateFieldsException.class,
            EntityNotFoundException.class,
            EmailAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final RuntimeException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage());
    }
}
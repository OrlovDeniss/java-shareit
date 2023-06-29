package ru.practicum.shareit.util.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exception.booking.*;
import ru.practicum.shareit.util.exception.comment.CommentOwnItemException;
import ru.practicum.shareit.util.exception.comment.CommentWithoutBookingException;
import ru.practicum.shareit.util.exception.general.JsonUpdateFieldsException;
import ru.practicum.shareit.util.exception.general.UnsupportedStateException;
import ru.practicum.shareit.util.exception.item.ItemNotAvailableException;
import ru.practicum.shareit.util.exception.item.ItemNotFoundException;
import ru.practicum.shareit.util.exception.request.RequestNotFoundException;
import ru.practicum.shareit.util.exception.user.UserEmailAlreadyExistsException;
import ru.practicum.shareit.util.exception.user.UserNotFoundException;
import ru.practicum.shareit.util.exception.user.UserOwnsObjectException;

import javax.persistence.EntityNotFoundException;
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
            BookingPatchConstraintException.class,
            BookingUpdateAccessException.class,
            CommentWithoutBookingException.class,
            ItemNotAvailableException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final RuntimeException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler({
            BookingAccessException.class,
            BookingNotFoundException.class,
            BookingItemOwnerException.class,
            BookingStatusPatchException.class,
            CommentOwnItemException.class,
            EntityNotFoundException.class,
            ItemNotFoundException.class,
            RequestNotFoundException.class,
            UserNotFoundException.class,
            UserOwnsObjectException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final RuntimeException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler({
            JsonUpdateFieldsException.class,
            UserEmailAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final RuntimeException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler(UnsupportedStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(final RuntimeException e) {
        log.info(e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        log.info(e.getMessage());
        return new ApiError("Неизвестная ошибка, обратитесь к логам.");
    }

}
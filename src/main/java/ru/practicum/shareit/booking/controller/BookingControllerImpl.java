package ru.practicum.shareit.booking.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.abstraction.userobject.controller.AbstractUserObjectController;
import ru.practicum.shareit.abstraction.userobject.service.UserObjectService;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingControllerImpl extends AbstractUserObjectController<BookingDto> implements BookingController {

    public BookingControllerImpl(UserObjectService<BookingDto> service) {
        super(service);
    }

    @Override
    protected BookingService getService() {
        return (BookingService) getSuperService();
    }
}

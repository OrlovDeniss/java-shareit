package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.abstraction.userobject.service.AbstractUserObjectService;

@Service
public class BookingServiceImpl extends AbstractUserObjectService<BookingDto, Booking> implements BookingService {

    protected BookingServiceImpl(ModelMapper<BookingDto, Booking> mapper,
                                 UserObjectRepository<Booking> objectRepo,
                                 UserRepository userRepo) {
        super(mapper, objectRepo, userRepo);
    }

    @Override
    protected BookingRepository getObjectRepository() {
        return (BookingRepository) getSuperObjectRepository();
    }

}

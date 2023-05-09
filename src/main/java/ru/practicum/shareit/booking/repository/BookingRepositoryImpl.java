package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.abstraction.userobject.repository.AbstractUserObjectRepository;

@Repository
public class BookingRepositoryImpl extends AbstractUserObjectRepository<Booking> implements BookingRepository {
}
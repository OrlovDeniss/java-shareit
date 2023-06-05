package ru.practicum.shareit.booking.service.finder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Waiting implements FinderStrategy {

    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findAllByUserId(Long userId) {
        return bookingRepository.findBookingsByUserIdWhereStatus(userId, Status.WAITING, sort);
    }

    @Override
    public List<Booking> findAllByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByOwnerIdWhereStatus(ownerId, Status.WAITING, sort);
    }

}
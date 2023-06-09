package ru.practicum.shareit.booking.service.finder;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class All implements FinderStrategy {

    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findAllByUserId(Long userId, Pageable pageable) {
        return bookingRepository.findAllByUserId(userId, pageable).toList();
    }

    @Override
    public List<Booking> findAllByOwnerId(Long ownerId, Pageable pageable) {
        return bookingRepository.findAllByOwnerId(ownerId, pageable).toList();
    }

}
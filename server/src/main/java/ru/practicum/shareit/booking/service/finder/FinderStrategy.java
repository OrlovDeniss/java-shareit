package ru.practicum.shareit.booking.service.finder;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface FinderStrategy {

    List<Booking> findAllByUserId(Long userId, Pageable pageable);

    List<Booking> findAllByOwnerId(Long ownerId, Pageable pageable);

}
package ru.practicum.shareit.booking.service.finder;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface FinderStrategy {

    Sort sort = Sort.by("start").descending();

    List<Booking> findAllByUserId(Long userId);

    List<Booking> findAllByOwnerId(Long ownerId);

}
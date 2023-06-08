package ru.practicum.shareit.booking.model;

import lombok.NonNull;

public interface BookingShort {

    @NonNull
    Long getId();

    void setId(Long id);

    @NonNull
    Long getBookerId();

    void setBookerId(Long bookerId);

    @NonNull
    Long getItemId();

    void setItemId(Long itemId);

}
package ru.practicum.shareit.booking;

import java.util.Optional;

public enum State {
    ALL,
    FUTURE,
    CURRENT,
    PAST,
    WAITING,
    REJECTED,
    UNKNOWN;

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }

}
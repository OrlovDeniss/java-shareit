package ru.practicum.shareit.booking.state;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.State;

@Component
public class StringToStateConverter implements Converter<String, State> {
    @Override
    public State convert(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            return State.UNKNOWN;
        }
    }
}
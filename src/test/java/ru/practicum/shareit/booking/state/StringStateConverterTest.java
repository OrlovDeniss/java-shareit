package ru.practicum.shareit.booking.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringStateConverterTest {

    private final StringToStateConverter stringToStateConverter = new StringToStateConverter();

    @Test
    void convert_whenStateIsExists_thenReturnState() {
        String state = "ALL";
        State convertedState = stringToStateConverter.convert(state);
        assertEquals(State.ALL, convertedState);
    }

    @Test
    void convert_whenStateNotExists_theReturnUnknownState() {
        String state = "RUN";
        State convertedState = stringToStateConverter.convert(state);
        assertEquals(State.UNKNOWN, convertedState);
    }

}
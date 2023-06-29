package ru.practicum.shareit.booking.service.finder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.util.exception.general.UnsupportedStateException;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FinderStrategyFactory {

    private final Map<String, FinderStrategy> finderStrategyMap;

    public FinderStrategy findStrategyByState(State status) {
        if (finderStrategyMap.containsKey(status.name().toLowerCase())) {
            return finderStrategyMap.get(status.name().toLowerCase());
        } else {
            throw new UnsupportedStateException();
        }
    }

}
package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.abstraction.userobject.repository.AbstractUserObjectRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl extends AbstractUserObjectRepository<Item> implements ItemRepository {

    @Override
    public List<Item> search(String text) {
        String lowerText = text.toLowerCase();
        Set<Item> items = new TreeSet<>(Comparator.comparingLong((Item::getId)));
        items.addAll(containsTextByName(lowerText));
        items.addAll(containsTextByDescription(lowerText));
        log.info("search '{}': {}",text, items);
        return new ArrayList<>(items);
    }

    private List<Item> containsTextByName(String text) {
        return data.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text))
                .filter(i -> i.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    private List<Item> containsTextByDescription(String text) {
        return data.values().stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text))
                .filter(i -> i.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}
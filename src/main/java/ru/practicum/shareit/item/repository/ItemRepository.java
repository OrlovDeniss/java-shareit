package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;

import java.util.List;

public interface ItemRepository extends UserObjectRepository<Item> {

    List<Item> search(String text);

}
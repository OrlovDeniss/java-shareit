package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.abstraction.userobject.service.AbstractUserObjectService;

import java.util.List;

@Service
public class ItemServiceImpl extends AbstractUserObjectService<ItemDto, Item> implements ItemService {

    protected ItemServiceImpl(ModelMapper<ItemDto, Item> mapper,
                              ItemRepository objectRepo,
                              UserRepository userRepo) {
        super(mapper, objectRepo, userRepo);
    }

    @Override
    public ItemRepository getObjectRepository() {
        return (ItemRepository) getSuperObjectRepository();
    }

    @Override
    public List<ItemDto> search(String text) {
        return mapper.toDto(getObjectRepository().search(text));
    }
}
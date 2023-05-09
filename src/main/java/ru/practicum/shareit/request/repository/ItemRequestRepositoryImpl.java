package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.abstraction.userobject.repository.AbstractUserObjectRepository;
import ru.practicum.shareit.request.model.ItemRequest;

@Repository
public class ItemRequestRepositoryImpl extends AbstractUserObjectRepository<ItemRequest>
        implements ItemRequestRepository {
}

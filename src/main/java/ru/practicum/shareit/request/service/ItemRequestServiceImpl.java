package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.mapper.ModelMapper;
import ru.practicum.shareit.abstraction.userobject.repository.UserObjectRepository;
import ru.practicum.shareit.abstraction.userobject.service.AbstractUserObjectService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class ItemRequestServiceImpl
        extends AbstractUserObjectService<ItemRequestDto, ItemRequest>
        implements ItemRequestService {

    protected ItemRequestServiceImpl(ModelMapper<ItemRequestDto, ItemRequest> mapper,
                                     UserObjectRepository<ItemRequest> objectRepo,
                                     UserRepository userRepo) {
        super(mapper, objectRepo, userRepo);
    }

    @Override
    protected ItemRequestRepository getObjectRepository() {
        return (ItemRequestRepository) getSuperObjectRepository();
    }
}

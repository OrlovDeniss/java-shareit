package ru.practicum.shareit.request.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.abstraction.userobject.controller.AbstractUserObjectController;
import ru.practicum.shareit.abstraction.userobject.service.UserObjectService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestControllerImpl extends AbstractUserObjectController<ItemRequestDto>
        implements ItemRequestController {

    public ItemRequestControllerImpl(UserObjectService<ItemRequestDto> service) {
        super(service);
    }

    @Override
    protected UserObjectService<ItemRequestDto> getService() {
        return null;
    }
}

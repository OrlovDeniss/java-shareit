package ru.practicum.shareit.abstraction.userobject.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

public interface UserObjectController<D> {

    String USER_ID = "X-Sharer-User-Id";

    D get(@PathVariable @Positive Long id,
          @RequestHeader(value = USER_ID) @Positive Long userId);

    D add(@Valid @RequestBody D d,
          @RequestHeader(value = USER_ID) @Positive Long userId);

    D update(@Valid @RequestBody D d,
             @RequestHeader(value = USER_ID) @Positive Long userId);

    D patch(@PathVariable @Positive Long id,
            @RequestBody Map<String, Object> fields,
            @RequestHeader(value = USER_ID) @Positive Long userId);

    List<D> findAllByUserId(@RequestHeader(value = USER_ID) @Positive Long userId);

    void delete(@PathVariable @Positive Long id,
                @RequestHeader(value = USER_ID) @Positive Long userId);

}
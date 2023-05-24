package ru.practicum.shareit.abstraction.userobject.controller;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

public interface UserObjectController<I, O> {

    String USER_ID = "X-Sharer-User-Id";

    @GetMapping("{id}")
    O get(@PathVariable("id") @Positive Long objectId,
          @RequestHeader(value = USER_ID) @Positive Long userId);

    @PostMapping
    O add(@Valid @RequestBody I in,
          @RequestHeader(value = USER_ID) @Positive Long userId);

    @PutMapping
    O update(@Valid @RequestBody I in,
             @RequestHeader(value = USER_ID) @Positive Long userId);

    @PatchMapping("{id}")
    O patch(@PathVariable("id") @Positive Long objectId,
            @RequestBody Map<String, Object> fields,
            @RequestHeader(value = USER_ID) @Positive Long userId);

    @GetMapping
    List<O> findAllByUserId(@RequestHeader(value = USER_ID) @Positive Long userId);

    @DeleteMapping("{id}")
    void delete(@PathVariable("id") @Positive Long objectId,
                @RequestHeader(value = USER_ID) @Positive Long userId);

}
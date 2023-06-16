package ru.practicum.shareit.util.pagerequest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequester extends PageRequest {

    public PageRequester(int from, int size, Sort sort) {
        super(from / size, size, sort);
    }

}
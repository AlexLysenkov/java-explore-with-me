package ru.practicum.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    public CustomPageRequest(Integer page, Integer size) {
        super(page, size, Sort.unsorted());
    }
}

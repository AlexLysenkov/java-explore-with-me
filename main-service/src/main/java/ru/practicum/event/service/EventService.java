package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.model.EventSort;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventByInitiator(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminDto updateEventAdminDto);

    List<EventShortDto> getAllEventsByInitiatorId(Long userId, Integer from, Integer size);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                        Integer size);

    EventFullDto getEventById(Long eventId, String uri, String ip);

    List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                          Boolean onlyAvailable, EventSort sort, Integer from, Integer size,
                                          String uri, String ip);

    EventFullDto getEventByIdAndUserId(Long eventId, Long userId);
}

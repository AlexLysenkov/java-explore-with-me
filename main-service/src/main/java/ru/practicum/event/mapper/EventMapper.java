package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class EventMapper {
    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .title(event.getTitle())
                .category(CategoryMapper.categoryToDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.userToShortDto(event.getInitiator()))
                .location(LocationMapper.locationToDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.categoryToDto(event.getCategory()))
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.userToShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .build();
    }

    public Event dtoToEvent(NewEventDto newEventDto) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.dtoToLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .build();
    }

    public List<EventShortDto> toEventShortDtoList(List<Event> events) {
        List<EventShortDto> result = new ArrayList<>();

        for (Event e : events) {
            result.add(EventMapper.toEventShortDto(e));
        }

        return result;
    }
}

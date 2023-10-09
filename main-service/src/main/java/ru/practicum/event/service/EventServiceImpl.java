package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.utils.CustomPageRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private static final String USER_NOT_FOUND = "User с id: %d не найден";
    private static final String EVENT_NOT_FOUND = "Event с id: %d не найден";
    private static final String CATEGORY_NOT_FOUND = "Категория не найдена";

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        Event event = EventMapper.dtoToEvent(newEventDto);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Событие не должно быть раньше, чем через 2 часа после создания публикации");
        }
        Location location = findLocation(LocationMapper.dtoToLocation(newEventDto.getLocation()));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new ObjectNotFoundException(CATEGORY_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId)));
        event.setLocation(location);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(user);
        event.setState(EventState.PENDING);
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        Event newEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(newEvent);
        log.info("Event с id: {} создан", event.getId());
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByInitiator(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto) {
        checkUserExistsById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(EVENT_NOT_FOUND, eventId)));
        if (!event.getState().equals(EventState.CANCELED) && !event.getState().equals(EventState.PENDING) &&
                event.getState() != null) {
            throw new ConflictException("Невозможно обновить событие");
        }
        if (updateEventUserDto.getEventDate() != null && updateEventUserDto.getEventDate()
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Событие не должно быть раньше, чем через 2 часа после создания публикации");
        }
        if (updateEventUserDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserDto.getCategory())
                    .orElseThrow(() -> new ObjectNotFoundException(CATEGORY_NOT_FOUND));
            event.setCategory(category);
        }
        if (updateEventUserDto.getAnnotation() != null) {
            event.setAnnotation(updateEventUserDto.getAnnotation());
        }
        if (updateEventUserDto.getDescription() != null) {
            event.setDescription(updateEventUserDto.getDescription());
        }
        if (updateEventUserDto.getTitle() != null) {
            event.setTitle(updateEventUserDto.getTitle());
        }
        if (updateEventUserDto.getEventDate() != null) {
            event.setEventDate(updateEventUserDto.getEventDate());
        }
        if (updateEventUserDto.getLocation() != null) {
            event.setLocation(locationRepository.findByLatAndLon(updateEventUserDto.getLocation().getLat(),
                    updateEventUserDto.getLocation().getLon()).orElseGet(() ->
                    locationRepository.save(LocationMapper.dtoToLocation(updateEventUserDto.getLocation()))));
        }
        if (updateEventUserDto.getPaid() != null) {
            event.setPaid(updateEventUserDto.getPaid());
        }
        if (updateEventUserDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserDto.getParticipantLimit());
        }
        if (updateEventUserDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserDto.getRequestModeration());
        }
        if (updateEventUserDto.getStateAction() != null) {
            if (updateEventUserDto.getStateAction().equals(EventStateActionUser.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            } else if (updateEventUserDto.getStateAction().equals(EventStateActionUser.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            }
        }
        log.info("Event с id {} от user c id {} обновлен", eventId, userId);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminDto updateEventAdminDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(EVENT_NOT_FOUND, eventId)));
        if (updateEventAdminDto.getStateAction() != null) {
            if (updateEventAdminDto.getStateAction().equals(EventStateActionAdmin.PUBLISH_EVENT)) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Неправильное состояние события");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventAdminDto.getStateAction().equals(EventStateActionAdmin.REJECT_EVENT)) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ConflictException("Событие уже опубликовано");
                } else {
                    event.setState(EventState.CANCELED);
                }
            }
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Событие должно начаться не раньше, чем через час после публикации");
        }
        if (updateEventAdminDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminDto.getCategory())
                    .orElseThrow(() -> new ObjectNotFoundException(CATEGORY_NOT_FOUND));
            event.setCategory(category);
        }
        if (updateEventAdminDto.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminDto.getAnnotation());
        }
        if (updateEventAdminDto.getDescription() != null) {
            event.setDescription(updateEventAdminDto.getDescription());
        }
        if (updateEventAdminDto.getTitle() != null) {
            event.setTitle(updateEventAdminDto.getTitle());
        }
        if (updateEventAdminDto.getEventDate() != null) {
            event.setEventDate(updateEventAdminDto.getEventDate());
        }
        if (updateEventAdminDto.getLocation() != null) {
            event.setLocation(locationRepository.findByLatAndLon(updateEventAdminDto.getLocation().getLat(),
                    updateEventAdminDto.getLocation().getLon()).orElseGet(() ->
                    locationRepository.save(LocationMapper.dtoToLocation(updateEventAdminDto.getLocation()))));
        }
        if (updateEventAdminDto.getPaid() != null) {
            event.setPaid(updateEventAdminDto.getPaid());
        }
        if (updateEventAdminDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminDto.getParticipantLimit());
        }
        if (updateEventAdminDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminDto.getRequestModeration());
        }
        log.info("Event с id {} от Admin обновлен", eventId);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getAllEventsByInitiatorId(Long userId, Integer from, Integer size) {
        checkUserExistsById(userId);
        Pageable pageable = new CustomPageRequest(from, size);
        List<Event> eventList = eventRepository.findAllByInitiatorId(userId, pageable);
        List<EventShortDto> eventShortList = eventList.stream().map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        Map<Long, Long> views = getViews(eventList);
        eventShortList.forEach(event -> {
            if (views.get(event.getId()) != null) {
                event.setViews(views.get(event.getId()));
            }
        });
        eventShortList.forEach(event -> event.setConfirmedRequests(getConfirmedRequest(event.getId())));
        log.info("Получен список events from = {} size = {} от  user с id {}", from, size, userId);
        return eventShortList;
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                               Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала не может быть после даты конца");
        }
        if (states != null) {
            for (String state : states) {
                try {
                    EventState.valueOf(state);
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Неизвестный статус: " + state);
                }
            }
        }
        Pageable pageable = new CustomPageRequest(from, size);
        List<Event> eventList = eventRepository.findEventsByAdmin(users, states, categories, rangeStart, rangeEnd,
                pageable);
        List<EventFullDto> eventFullList = setFullDtoAdditionalFields(eventList);
        log.info("Получен список events от admin");
        return eventFullList;
    }

    @Override
    public EventFullDto getEventById(Long eventId, String uri, String ip) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(EVENT_NOT_FOUND, eventId)));
        statsClient.saveStats(EndpointHitDto.builder()
                .app("main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build());
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ObjectNotFoundException("Event не найден");
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event));
        Map<Long, Long> views = getViews(List.of(event));
        eventFullDto.setViews(views.getOrDefault(eventFullDto.getId(), 0L));
        eventFullDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventFullDto.getId(), 0L));
        log.info("Получен список events от admin с id {}", eventId);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                 Boolean onlyAvailable, EventSort sort, Integer from, Integer size,
                                                 String uri, String ip) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала не может быть после даты конца");
        }
        statsClient.saveStats(EndpointHitDto.builder()
                .app("main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build());
        Pageable pageable = new CustomPageRequest(from, size);
        List<Event> eventList = eventRepository.findEventsByUser(text, categories, paid, rangeStart != null ?
                rangeStart : LocalDateTime.now(), rangeEnd, pageable);
        List<EventShortDto> eventShortDtoList = setShortDtoAdditionalFields(eventList);
        Map<Long, Integer> eventsParticipant = new HashMap<>();
        eventList.forEach(event -> eventsParticipant.put(event.getId(), event.getParticipantLimit()));
        if (onlyAvailable) {
            eventShortDtoList = eventShortDtoList.stream().filter(eventShortDto ->
                    (eventsParticipant.get(eventShortDto.getId()) == 0 || eventsParticipant.get(eventShortDto.getId())
                            > eventShortDto.getConfirmedRequests())).collect(Collectors.toList());
        }
        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    eventList = eventRepository.findEventsSortedByEventDate(text, categories, paid,
                            rangeStart, rangeEnd, pageable);
                    return eventList.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
                case VIEWS:
                    eventShortDtoList.sort(Comparator.comparing(EventShortDto::getViews));
                    break;
                default:
                    throw new ValidationException("Невалидный параметр");
            }
        }
        log.info("Получен список events от user");
        return eventShortDtoList;
    }

    private List<EventFullDto> setFullDtoAdditionalFields(List<Event> eventList) {
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventList);
        Map<Long, Long> views = getViews(eventList);
        return eventList.stream().map(event ->
                EventMapper.eventToFullDto(event, views.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L))).collect(Collectors.toList());
    }

    private List<EventShortDto> setShortDtoAdditionalFields(List<Event> eventList) {
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventList);
        Map<Long, Long> views = getViews(eventList);
        return eventList.stream().map(event ->
                EventMapper.eventToShortDto(event, views.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L))).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdAndUserId(Long eventId, Long userId) {
        checkUserExistsById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(EVENT_NOT_FOUND, eventId)));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event));
        Map<Long, Long> views = getViews(List.of(event));
        eventFullDto.setViews(views.getOrDefault(eventFullDto.getId(), 0L));
        eventFullDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventFullDto.getId(), 0L));
        log.info("Получен event по id {} от user c id {}", eventId, userId);
        return eventFullDto;
    }

    private Location findLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));
    }

    private void checkUserExistsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId));
        }
    }

    private Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();
        List<Event> publishedEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getPublishedOn() != null) {
                publishedEvents.add(event);
            }
        }
        Optional<LocalDateTime> minPublish = publishedEvents.stream().map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo);
        if (minPublish.isPresent()) {
            LocalDateTime start = minPublish.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = publishedEvents.stream().map(event -> "/events/" + event.getId())
                    .collect(Collectors.toList());
            List<ViewStatsDto> statsDto = statsClient.getStats(start, end, uris, true);
            statsDto.forEach(stat -> {
                Long eventId = Long.parseLong(stat.getUri().substring(stat.getUri().lastIndexOf("/") + 1));
                views.put(eventId, stat.getHits());
            });
        }
        return views;
    }

    private Long getConfirmedRequest(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Event> eventList = events.stream().filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
        return requestRepository.findAllByEventInAndStatus(eventList, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId(), Collectors.counting()));
    }
}

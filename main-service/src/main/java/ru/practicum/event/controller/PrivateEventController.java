package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@Validated
@Slf4j
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Получен POST запрос по эндпоинту '/users/{}/events' на добавление event {}", userId, newEventDto);
        return new ResponseEntity<>(eventService.createEvent(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEventsByInitiatorId(@PathVariable Long userId,
                                                                         @RequestParam(defaultValue = "0",
                                                                                 required = false)
                                                                         @PositiveOrZero Integer from,
                                                                         @RequestParam(defaultValue = "10",
                                                                                 required = false)
                                                                         @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту '/users/{}/events' на получение events от инициатора", userId);
        return ResponseEntity.ok(eventService.getAllEventsByInitiatorId(userId, from, size));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventByIdAndUserId(@PathVariable Long eventId, @PathVariable Long userId) {
        log.info("Получен GET запрос по эндпоинту '/users/{}/events/{}' на получение event с id {}", userId, eventId,
                eventId);
        return ResponseEntity.ok(eventService.getEventByIdAndUserId(eventId, userId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId,
                                                               @Valid
                                                               @RequestBody UpdateEventUserDto updateEventUserDto) {
        log.info("Получен PATCH запрос по эндпоинту '/users/{}/events/{}' на обновление event {}", userId, eventId,
                updateEventUserDto);
        return ResponseEntity.ok(eventService.updateEventByInitiator(userId, eventId, updateEventUserDto));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResultDto> updateRequestStatus(@PathVariable Long userId,
                                                                                 @PathVariable Long eventId,
                                                                                 @Valid @RequestBody
                                                                                 EventRequestStatusUpdateRequestDto
                                                                                         request) {
        log.info("Получен PATCH запрос по эндпоинту '/users/{}/events/{}/requests' на обновление request {}", userId,
                eventId, request);
        return ResponseEntity.ok(requestService.updateRequestStatus(userId, eventId, request));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByOwner(@PathVariable Long userId,
                                                                            @PathVariable Long eventId) {
        log.info("Получен GET запрос по эндпоинту '/users/{}/events/{}/requests' на получение запросов " +
                "ParticipationRequestDto", userId, eventId);
        return ResponseEntity.ok(requestService.getRequestsByOwner(userId, eventId));
    }
}

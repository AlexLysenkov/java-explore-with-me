package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Validated
@Slf4j
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получен GET запрос по эндпоинту '/events' на получение event с id {}", id);
        return ResponseEntity.ok(eventService.getEventById(id, request.getRequestURI(), request.getRemoteAddr()));
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsByPublic(@RequestParam(required = false) String text,
                                                                 @RequestParam(required = false) List<Long> categories,
                                                                 @RequestParam(required = false) Boolean paid,
                                                                 @RequestParam(required = false)
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                 LocalDateTime rangeStart,
                                                                 @RequestParam(required = false)
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                 LocalDateTime rangeEnd,
                                                                 @RequestParam(required = false, defaultValue = "false")
                                                                 Boolean onlyAvailable,
                                                                 @RequestParam(required = false) EventSort sort,
                                                                 @RequestParam(required = false, defaultValue = "0")
                                                                 @PositiveOrZero Integer from,
                                                                 @RequestParam(required = false, defaultValue = "10")
                                                                 @Positive Integer size,
                                                                 HttpServletRequest request) {
        log.info("Получен GET запрос по эндпоинту '/events' на получение events");
        return ResponseEntity.ok(eventService.getEventsByPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request.getRequestURI(), request.getRemoteAddr()));
    }
}

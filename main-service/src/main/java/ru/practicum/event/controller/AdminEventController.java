package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Validated
@Slf4j
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByAdmin(@PathVariable Long eventId,
                                                           @RequestBody @Valid UpdateEventAdminDto updateEventAdminDto) {
        log.info("Получен PATCH запрос по эндпоинту '/admin/events' на обновление event с id {} на {}", eventId,
                updateEventAdminDto);
        return ResponseEntity.ok(eventService.updateEventByAdmin(eventId, updateEventAdminDto));
    }

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                               @RequestParam(required = false) List<String> states,
                                                               @RequestParam(required = false) List<Long> categories,
                                                               @RequestParam(required = false)
                                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                               LocalDateTime rangeStart,
                                                               @RequestParam(required = false)
                                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                               LocalDateTime rangeEnd,
                                                               @RequestParam(required = false, defaultValue = "0")
                                                               @PositiveOrZero Integer from,
                                                               @RequestParam(required = false, defaultValue = "10")
                                                               @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту '/admin/events' на получение списка event");
        return ResponseEntity.ok(eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from,
                size));
    }
}

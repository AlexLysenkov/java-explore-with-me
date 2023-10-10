package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Controller
@Validated
@Slf4j
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable Long userId,
                                                                 @RequestParam Long eventId) {
        log.info("Получен POST запрос по эндпоинту '/users/{}/requests' на добавление request на event с id {}",
                userId, eventId);
        return new ResponseEntity<>(requestService.createRequest(userId, eventId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByUserId(@PathVariable Long userId) {
        log.info("Получен GET запрос по эндпоинту '/users/{}/requests' на получение requests от user с id {}", userId,
                userId);
        return ResponseEntity.ok(requestService.getRequestsByUserId(userId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        log.info("Получен PATCH запрос по эндпоинту '/users/{}/requests' на отмену request с id {} " +
                "от user с id {}", userId, requestId, userId);
        return ResponseEntity.ok(requestService.cancelRequest(userId, requestId));
    }
}

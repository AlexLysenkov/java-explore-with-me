package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@Validated
@Slf4j
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllCommentsByEventId(@PathVariable Long eventId,
                                                                    @RequestParam(required = false, defaultValue = "0")
                                                                    @PositiveOrZero Integer from,
                                                                    @RequestParam(required = false, defaultValue = "10")
                                                                    @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту /events/{}/comments на получение comments по event с id {}",
                eventId, eventId);
        return ResponseEntity.ok(commentService.getAllCommentsByEventId(eventId, from, size));
    }
}

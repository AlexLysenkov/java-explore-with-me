package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@Controller
@Validated
@Slf4j
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long userId, @PathVariable Long eventId,
                                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Получен POST запрос по эндпоинту '/users/{}/comments/{}' на создание comment {}", userId, eventId,
                newCommentDto);
        return new ResponseEntity<>(commentService.createComment(userId, eventId, newCommentDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long userId, @PathVariable Long commentId,
                                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Получен PATCH запрос по эндпоинту '/users/{}/comments/{}' на обновление comment на {}",
                userId, commentId, newCommentDto);
        return ResponseEntity.ok(commentService.updateComment(userId, commentId, newCommentDto));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentByIdAndByUser(@PathVariable Long userId, @PathVariable Long commentId)
    {
        log.info("Получен GET запрос по эндпоинту '/users/{}/comments/{}' на получение comment", userId, commentId);
        return ResponseEntity.ok(commentService.getCommentByIdAndByUser(userId, commentId));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllUserComments(@PathVariable Long userId) {
        log.info("Получен GET запрос по эндпоинту '/users/{}/comments/' на получение всех comments по user с id {}",
                userId, userId);
        return ResponseEntity.ok(commentService.getAllUserComments(userId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteCommentByIdByUser(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteCommentByIdByUser(userId, commentId);
        log.info("Получен DELETE запрос по эндпоинту '/users/{}/comments/{}' на удаление comment по id {}",
                userId, commentId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

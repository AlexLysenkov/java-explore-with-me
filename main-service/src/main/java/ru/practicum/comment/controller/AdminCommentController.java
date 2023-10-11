package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.comment.service.CommentService;

@Controller
@Validated
@Slf4j
@RequestMapping("/admin/comments/{commentId}")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping
    public ResponseEntity<?> deleteCommentByIdByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByIdByAdmin(commentId);
        log.info("Получен DELETE запрос по эндпоинту '/admin/comments/{}' на удаление comment по id {}", commentId,
                commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

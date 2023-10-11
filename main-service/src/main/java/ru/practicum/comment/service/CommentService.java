package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto);

    CommentDto getCommentByIdAndByUser(Long userId, Long commentId);

    List<CommentDto> getAllUserComments(Long userId);

    List<CommentDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size);

    void deleteCommentByIdByUser(Long userId, Long commentId);

    void deleteCommentByIdByAdmin(Long commentId);
}

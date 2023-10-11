package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.utils.CustomPageRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private static final String USER_NOT_FOUND = "User с id: %d не найден";
    private static final String COMMENT_NOT_FOUND = "Comment с id: %d не найден";
    private static final String EVENT_NOT_FOUND = "Event с id: %d не найден";

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(EVENT_NOT_FOUND, eventId)));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя оставлять comment для неопубликованного event");
        }
        Comment comment = CommentMapper.dtoToComment(newCommentDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        log.info("Comment {}, c user id = {} и с event id = {}, создан", newCommentDto, userId, eventId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        checkUserExistsById(userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Нельзя редактировать чужой comment");
        }
        if (newCommentDto.getMessage() != null && !newCommentDto.getMessage().isBlank()) {
            comment.setMessage(newCommentDto.getMessage());
        }
        log.info("Comment с id: {} от user с id: {} обновлен", commentId, userId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentByIdAndByUser(Long userId, Long commentId) {
        checkUserExistsById(userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Нельзя получить comment, созданный другим user");
        }
        log.info("Comment с id: {} от user с id: {} получен", commentId, userId);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getAllUserComments(Long userId) {
        checkUserExistsById(userId);
        List<CommentDto> commentDtoList = CommentMapper.listCommentsToListDto(
                commentRepository.findAllByAuthorId(userId));
        log.info("Получены все comments от user с id: {}", userId);
        return commentDtoList;
    }

    @Override
    public List<CommentDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size) {
        checkEventExistsById(eventId);
        Pageable pageable = new CustomPageRequest(from, size);
        List<CommentDto> commentDtoList = CommentMapper.listCommentsToListDto(
                commentRepository.findAllByEventId(eventId, pageable)
        );
        log.info("Получены все comments по event с id: {}", eventId);
        return commentDtoList;
    }

    @Override
    @Transactional
    public void deleteCommentByIdByUser(Long userId, Long commentId) {
        checkUserExistsById(userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(COMMENT_NOT_FOUND, commentId)));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Нельзя удалить comment, созданный другим user");
        }
        commentRepository.deleteById(commentId);
        log.info("Comment с id: {}, от user с id: {}, удален", commentId, userId);
    }

    @Override
    @Transactional
    public void deleteCommentByIdByAdmin(Long commentId) {
        checkCommentExistsById(commentId);
        commentRepository.deleteById(commentId);
        log.info("Comment с id: {} удален админом", commentId);
    }

    private void checkUserExistsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId));
        }
    }

    private void checkEventExistsById(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException(String.format(EVENT_NOT_FOUND, eventId));
        }
    }

    private void checkCommentExistsById(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ObjectNotFoundException(String.format(COMMENT_NOT_FOUND, commentId));
        }
    }
}

package ru.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .message(comment.getMessage())
                .created(comment.getCreated())
                .build();
    }

    public Comment dtoToComment(NewCommentDto newCommentDto) {
        return Comment.builder()
                .message(newCommentDto.getMessage())
                .created(LocalDateTime.now())
                .build();
    }

    public List<CommentDto> listCommentsToListDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}

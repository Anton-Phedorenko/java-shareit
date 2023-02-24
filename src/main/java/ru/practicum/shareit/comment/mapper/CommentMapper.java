package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(comment.getId());
        comment.setText(commentDto.getText());
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static ItemDto.Comment toItemComment(Comment comment) {
        return new ItemDto.Comment(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<ItemDto.Comment> toListItemCommentDto(List<Comment> comments) {
        return comments
                .stream()
                .map(CommentMapper::toItemComment)
                .collect(Collectors.toList());
    }
}

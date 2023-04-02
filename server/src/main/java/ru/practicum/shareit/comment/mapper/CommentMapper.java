package ru.practicum.shareit.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDtoOutput;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(comment.getId());
        comment.setText(commentDto.getText());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static ItemDtoOutput.Comment toItemComment(Comment comment) {
        return new ItemDtoOutput.Comment(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<ItemDtoOutput.Comment> toListItemCommentDto(List<Comment> comments) {
        return comments
                .stream()
                .map(CommentMapper::toItemComment)
                .collect(Collectors.toList());
    }
}

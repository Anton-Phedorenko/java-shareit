package ru.practicum.shareit.comment.service.dal;

import ru.practicum.shareit.comment.dto.CommentDto;

public interface CommentService {
    CommentDto create(CommentDto commentDto, Long itemId, Long userId);
}

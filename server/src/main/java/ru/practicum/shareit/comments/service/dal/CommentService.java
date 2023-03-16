package ru.practicum.shareit.comments.service.dal;

import ru.practicum.shareit.comments.dto.CommentDto;

public interface CommentService {
    CommentDto create(CommentDto commentDto, Long itemId, Long userId);
}

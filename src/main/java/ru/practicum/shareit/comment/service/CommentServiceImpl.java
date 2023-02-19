package ru.practicum.shareit.comment.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    public CommentServiceImpl(CommentRepository commentRepository, ItemService itemService, BookingRepository bookingRepository) {
        this.commentRepository = commentRepository;
        this.itemService = itemService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public CommentDto create(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemService.findItemById(itemId);
        List<Booking> bookings = bookingRepository.getAllByBookerPast(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .collect(Collectors.toList());
        if (bookings.size() != 0) {
            Comment comment = commentRepository.save(CommentMapper.toComment(commentDto));
            comment.setAuthor(bookings.get(0).getBooker());
            comment.setItem(item);
            return CommentMapper.toCommentDto(comment);
        } else {
            throw new BadRequestException("Предмет не бронировался или ожидает бронирования");
        }
    }
}

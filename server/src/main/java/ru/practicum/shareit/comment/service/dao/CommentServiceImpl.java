package ru.practicum.shareit.comment.service.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.dal.CommentService;
import ru.practicum.shareit.exeption.exeptions.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.dal.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public CommentDto create(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemService.getByIdForItem(itemId);
        List<Booking> bookings = getBookings(userId, itemId);
        if (bookings.size() != 0) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setCreated(LocalDateTime.now());
            comment.setAuthor(bookings.get(0).getBooker());
            comment.setItem(item);
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new BadRequestException("Предмет не бронировался или ожидает бронирования");
        }
    }

    private List<Booking> getBookings(Long userId, Long itemId) {
        return bookingRepository.getAllByBookerPast(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(0, 100, Sort.by(DESC, "start")))
                .stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .collect(Collectors.toList());
    }
}
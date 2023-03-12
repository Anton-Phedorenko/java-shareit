package ru.practicum.shareit.comment.service.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.comment.service.CommentServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    private User booker;
    private Item item;
    private Booking booking;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepository, itemService, bookingRepository);
        booker = new User();
        booker.setId(1L);
        booker.setName("Макс");
        booker.setEmail("max@yandex.ru");

        User owner = new User();
        owner.setId(2L);
        owner.setName("Антон");
        owner.setEmail("anton@yandex.ru");

        item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Ракетка");
        item.setAvailable(true);
        item.setDescription("Теннисная ракетка");

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2022, 12, 8, 8, 0));
        booking.setEnd(LocalDateTime.of(2022, 12, 9, 8, 0));
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2022, 12, 9, 12, 0));
        comment.setText("Качественная ракетка");

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setAuthorName(booker.getName());
        commentDto.setCreated(LocalDateTime.of(2022, 12, 9, 12, 0));
        commentDto.setText("Качественная ракетка");
    }

    @Test
    void createTest() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(itemService.getItemById(anyLong()))
                .thenReturn(item);
        Mockito
                .when(bookingRepository.findAllByBookerPast(anyLong(),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(bookings);
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentDtoNew = commentService.create(commentDto, item.getId(), booker.getId());
        Assertions.assertEquals(comment.getId(), commentDtoNew.getId());
        Assertions.assertEquals(comment.getAuthor().getName(), commentDtoNew.getAuthorName());
        Assertions.assertEquals(comment.getText(), commentDtoNew.getText());
        Assertions.assertEquals(comment.getCreated(), commentDtoNew.getCreated());

    }
}

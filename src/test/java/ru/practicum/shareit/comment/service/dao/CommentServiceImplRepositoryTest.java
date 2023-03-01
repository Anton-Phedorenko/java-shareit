package ru.practicum.shareit.comment.service.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class CommentServiceImplRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findCommentForEmptyItems() {
        List<Comment> comments = commentRepository.findCommentForItems(List.of());
        assertThat(comments).isEmpty();
    }

    @Test
    void findCommentForItems() {
        User booker = new User();
        booker.setName("Макс");
        booker.setEmail("max@yandex.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("Антон");
        owner.setEmail("anton@yandex.ru");
        userRepository.save(owner);

        Item item = new Item();
        item.setOwner(owner);
        item.setName("Ракетка");
        item.setAvailable(true);
        item.setDescription("Теннисная ракетка");
        itemRepository.save(item);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.of(2022, 12, 8, 8, 0));
        booking.setEnd(LocalDateTime.of(2022, 12, 9, 8, 0));
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        Comment comment = new Comment();
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2022, 12, 9, 12, 0));
        comment.setText("Качественная ракетка");
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findCommentForItems(items);
        assertFalse(comments.isEmpty());
        assertEquals(comments.get(0).getId(), comment.getId());
        assertEquals(comments.get(0).getText(), comment.getText());
        assertEquals(comments.get(0).getAuthor().getId(), comment.getAuthor().getId());
        assertEquals(comments.get(0).getItem().getId(), comment.getItem().getId());
        assertEquals(comments.get(0).getCreated(), comment.getCreated());
    }
}

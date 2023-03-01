package ru.practicum.shareit.booking.service.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Transactional
@SpringBootTest(properties = "db.name=test",webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTestIntegration {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User booker;

    private User owner;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setName("Макс");
        booker.setEmail("max@yandex.ru");
        userRepository.save(booker);

        owner = new User();
        owner.setName("Антон");
        owner.setEmail("anton@yandex.ru");
        userRepository.save(owner);

        Item item = new Item();
        item.setOwner(owner);
        item.setName("Ракетка");
        item.setAvailable(true);
        item.setDescription("Теннисная ракетка");
        itemRepository.save(item);

        booking = new Booking();
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
    }

    @Test
    void getAllByOwnerAll() {
        List<Booking> bookings = bookingRepository.findAllByOwnerAll(owner.getId(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByOwnerPast() {
        List<Booking> bookings = bookingRepository.findAllByOwnerPast(owner.getId(), LocalDateTime.now(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByOwnerFuture() {
        List<Booking> bookings = bookingRepository.findAllByOwnerFuture(owner.getId(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByOwnerCurrent() {
        List<Booking> bookings = bookingRepository.findAllByOwnerCurrent(owner.getId(), LocalDateTime.now(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByOwnerRejected() {
        List<Booking> bookings = bookingRepository.findAllByOwnerRejected(owner.getId(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByOwnerWaiting() {
        List<Booking> bookings = bookingRepository.findAllByOwnerWaiting(owner.getId(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByBookerAll() {
        List<Booking> bookings = bookingRepository.findAllByBookerAll(booker.getId(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByBookerPast() {
        List<Booking> bookings = bookingRepository.findAllByBookerPast(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByBookerFuture() {
        List<Booking> bookings = bookingRepository.findAllByBookerFuture(booker.getId(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllByBookerCurrent() {
        List<Booking> bookings = bookingRepository.findAllByBookerCurrent(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByBookerRejected() {
        List<Booking> bookings = bookingRepository.findAllByBookerRejected(booker.getId(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByBookerWaiting() {
        List<Booking> bookings = bookingRepository.findAllByBookerWaiting(booker.getId(),
                PageRequest.of(0, 1, Sort.by(DESC, "start")));

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAll() {
        List<Booking> bookings = bookingRepository.findAll(Sort.by(DESC, "start"));

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findApprovedForItems() {
        List<Item> items = itemRepository.findAll();
        booking.setStatus(Status.APPROVED);
        List<Booking> bookings = bookingRepository.findApprovedForItems(items, Sort.by(DESC, "start"));

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }
}

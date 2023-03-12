package ru.practicum.shareit.comment.service.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreation;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentServiceImplTestIntegration {

    private final EntityManager em;
    private final CommentService commentService;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private User bookerNew;
    private ItemDto itemDtoOutput;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Антон");
        owner.setEmail("anton@yandex.ru");
        User ownerNew = userService.create(owner);

        User booker = new User();
        booker.setName("Макс");
        booker.setEmail("max@yandex.ru");
        bookerNew = userService.create(booker);

        ItemDto itemDtoInput = new ItemDto();
        itemDtoInput.setName("Ракетка");
        itemDtoInput.setDescription("Теннисная ракетка");
        itemDtoInput.setAvailable(true);
        itemDtoOutput = itemService.create(itemDtoInput, ownerNew.getId());

        BookingDtoCreation bookingDtoInput = new BookingDtoCreation();
        bookingDtoInput.setItemId(itemDtoOutput.getId());
        bookingDtoInput.setStart(LocalDateTime.of(2022, 12, 8, 8, 0));
        bookingDtoInput.setEnd(LocalDateTime.of(2022, 12, 9, 8, 0));
        BookingDto bookingDtoOutput = bookingService.create(bookingDtoInput, bookerNew.getId());
        bookingService.update(bookingDtoOutput.getId(), ownerNew.getId(), true);

        commentDto = new CommentDto();
        commentDto.setText("Удобная ракетка, советую");
    }

    @Test
    void create() {
        CommentDto commentDtoNew = commentService.create(commentDto, itemDtoOutput.getId(), bookerNew.getId());

        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", commentDtoNew.getId())
                .getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(bookerNew.getId(), comment.getAuthor().getId());
        assertEquals(bookerNew.getName(), comment.getAuthor().getName());
        assertEquals(bookerNew.getEmail(), comment.getAuthor().getEmail());
        assertEquals(itemDtoOutput.getId(), comment.getItem().getId());
        assertEquals(itemDtoOutput.getName(), comment.getItem().getName());
        assertEquals(itemDtoOutput.getDescription(), comment.getItem().getDescription());
        assertEquals(itemDtoOutput.getAvailable(), comment.getItem().getAvailable());
    }
}

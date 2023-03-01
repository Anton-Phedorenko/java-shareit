package ru.practicum.shareit.item.service.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    private User requestor;
    private User owner;
    private Item item;
    private ItemRequest request;
    private ItemDto itemDtoInput;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, commentRepository,
                requestRepository);
        requestor = new User();
        requestor.setId(1L);
        requestor.setName("Макс");
        requestor.setEmail("max@yandex.ru");

        owner = new User();
        owner.setId(2L);
        owner.setName("Антон");
        owner.setEmail("anton@yandex.ru");

        request = new ItemRequest();
        request.setId(1L);
        request.setCreated(LocalDateTime.of(2022, 12, 7, 8, 0));
        request.setDescription("Хочу теннисную ракетку");
        request.setRequestor(requestor);

        item = new Item();
        item.setId(1L);
        item.setName("Ракетка");
        item.setAvailable(true);
        item.setDescription("Теннисная ракетка");

        itemDtoInput = new ItemDto();
        itemDtoInput.setId(item.getId());
        itemDtoInput.setName(item.getName());
        itemDtoInput.setDescription(item.getDescription());
        itemDtoInput.setAvailable(item.getAvailable());
        itemDtoInput.setRequestId(request.getId());

        comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(requestor);
        comment.setText("Клевая штука");
        comment.setCreated(LocalDateTime.of(2022, 12, 10, 9, 0));
        comment.setItem(item);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2022, 12, 8, 8, 0));
        booking.setEnd(LocalDateTime.of(2022, 12, 10, 8, 0));
        booking.setStatus(Status.WAITING);
        booking.setBooker(requestor);
        booking.setItem(item);
    }

    @Test
    void createTest() {
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(owner);
        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        ItemDto itemDtoOutput = itemService.create(itemDtoInput, owner.getId());
        Assertions.assertEquals(item.getId(), itemDtoOutput.getId());
        Assertions.assertEquals(item.getName(), itemDtoOutput.getName());
        Assertions.assertEquals(item.getDescription(), itemDtoOutput.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDtoOutput.getAvailable());
        Assertions.assertEquals(item.getRequest().getId(), itemDtoOutput.getRequestId());

    }

    @Test
    void updateTest() {
        item.setOwner(owner);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(owner);
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(requestor);
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.findApprovedForItems(
                        items, Sort.by(DESC, "start")))
                .thenReturn(anyList());
        Mockito
                .when(commentRepository.findCommentForItems(items))
                .thenReturn(anyList());

        ItemDto itemDtoInputNew = new ItemDto();
        itemDtoInputNew.setId(item.getId());
        itemDtoInputNew.setName("Ракетка");
        itemDtoInputNew.setDescription(item.getDescription());
        itemDtoInputNew.setAvailable(item.getAvailable());
        itemDtoInputNew.setRequestId(request.getId());

        ItemDto itemDtoOutput = itemService.update(itemDtoInputNew, owner.getId());
        Assertions.assertEquals(item.getId(), itemDtoOutput.getId());
        Assertions.assertEquals(item.getName(), itemDtoOutput.getName());
        Assertions.assertEquals(item.getDescription(), itemDtoOutput.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDtoOutput.getAvailable());
        Assertions.assertEquals(request.getId(), itemDtoOutput.getRequestId());
    }

    @Test
    void updateWithOtherUserTest() {
        item.setOwner(owner);
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(owner);
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(requestor);
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.update(itemDtoInput, requestor.getId());
                });
        Assertions.assertEquals("У данной вещи другой владелец", ex.getMessage());

    }

    @Test
    void getByIdTest() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        item.setOwner(owner);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.findApprovedForItems(
                        items, Sort.by(DESC, "start")))
                .thenReturn(bookings);
        Mockito
                .when(commentRepository.findCommentForItems(items))
                .thenReturn(comments);

        ItemDto itemDtoOutput = itemService.getById(item.getId(), owner.getId());
        Assertions.assertEquals(item.getId(), itemDtoOutput.getId());
        Assertions.assertEquals(item.getName(), itemDtoOutput.getName());
        Assertions.assertEquals(item.getDescription(), itemDtoOutput.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDtoOutput.getAvailable());
        Assertions.assertEquals(comments.get(0).getId(), itemDtoOutput.getComments().get(0).getId());
        Assertions.assertEquals(comments.get(0).getText(), itemDtoOutput.getComments().get(0).getText());
        Assertions.assertEquals(comments.get(0).getAuthor().getName(),
                itemDtoOutput.getComments().get(0).getAuthorName());
        Assertions.assertEquals(comments.get(0).getCreated(), itemDtoOutput.getComments().get(0).getCreated());
        Assertions.assertEquals(bookings.get(0).getId(), itemDtoOutput.getLastBooking().getId());
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), itemDtoOutput.getLastBooking().getBookerId());
    }

    @Test
    void getByIdFromRequestorTest() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        item.setOwner(owner);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.findApprovedForItems(
                        items, Sort.by(DESC, "start")))
                .thenReturn(bookings);
        Mockito
                .when(commentRepository.findCommentForItems(items))
                .thenReturn(comments);

        ItemDto itemDtoOutput = itemService.getById(item.getId(), requestor.getId());
        Assertions.assertEquals(item.getId(), itemDtoOutput.getId());
        Assertions.assertEquals(item.getName(), itemDtoOutput.getName());
        Assertions.assertEquals(item.getDescription(), itemDtoOutput.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDtoOutput.getAvailable());
        Assertions.assertEquals(comments.get(0).getId(), itemDtoOutput.getComments().get(0).getId());
        Assertions.assertEquals(comments.get(0).getText(), itemDtoOutput.getComments().get(0).getText());
        Assertions.assertEquals(comments.get(0).getAuthor().getName(),
                itemDtoOutput.getComments().get(0).getAuthorName());
        Assertions.assertEquals(comments.get(0).getCreated(), itemDtoOutput.getComments().get(0).getCreated());
    }

    @Test
    void getAllTest() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        Booking bookingNext = new Booking();
        bookingNext.setId(1L);
        bookingNext.setStart(LocalDateTime.of(2023, 12, 15, 8, 0));
        bookingNext.setEnd(LocalDateTime.of(2023, 12, 17, 8, 0));
        bookingNext.setStatus(Status.WAITING);
        bookingNext.setBooker(requestor);
        bookingNext.setItem(item);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        bookings.add(bookingNext);

        item.setOwner(owner);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(owner);
        Mockito
                .when(itemRepository.getAll(owner.getId(),
                        PageRequest.of(0, 1, Sort.unsorted())))
                .thenReturn(items);
        Mockito
                .when(bookingRepository.findApprovedForItems(
                        items, Sort.by(DESC, "start")))
                .thenReturn(bookings);
        Mockito
                .when(commentRepository.findCommentForItems(items))
                .thenReturn(comments);

        List<ItemDto> itemDtoOutputList = itemService.getAll(owner.getId(), 0, 1);
        Assertions.assertEquals(item.getId(), itemDtoOutputList.get(0).getId());
        Assertions.assertEquals(item.getName(), itemDtoOutputList.get(0).getName());
        Assertions.assertEquals(item.getDescription(), itemDtoOutputList.get(0).getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDtoOutputList.get(0).getAvailable());
        Assertions.assertEquals(comments.get(0).getId(), itemDtoOutputList.get(0).getComments().get(0).getId());
        Assertions.assertEquals(comments.get(0).getText(), itemDtoOutputList.get(0).getComments().get(0).getText());
        Assertions.assertEquals(comments.get(0).getAuthor().getName(),
                itemDtoOutputList.get(0).getComments().get(0).getAuthorName());
        Assertions.assertEquals(comments.get(0).getCreated(), itemDtoOutputList.get(0).getComments().get(0)
                .getCreated());
        Assertions.assertEquals(bookings.get(0).getId(), itemDtoOutputList.get(0).getLastBooking().getId());
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), itemDtoOutputList.get(0).getLastBooking()
                .getBookerId());
        Assertions.assertEquals(bookings.get(1).getId(), itemDtoOutputList.get(0).getNextBooking().getId());
        Assertions.assertEquals(bookings.get(1).getBooker().getId(), itemDtoOutputList.get(0).getNextBooking()
                .getBookerId());
    }

    @Test
    void getByTextTest() {
        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito
                .when(itemRepository.findByText("ракетка", PageRequest.of(0, 1, Sort.unsorted())))
                .thenReturn(items);

        List<ItemDto> itemDtoOutputList = itemService.getByText("ракетка", 0, 1);
        Assertions.assertEquals(item.getId(), itemDtoOutputList.get(0).getId());
        Assertions.assertEquals(item.getName(), itemDtoOutputList.get(0).getName());
        Assertions.assertEquals(item.getDescription(), itemDtoOutputList.get(0).getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDtoOutputList.get(0).getAvailable());
    }

    @Test
    void getByIdForItemTest() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Item itemNew = itemService.getItemById(item.getId());
        Assertions.assertEquals(item.getId(), itemNew.getId());
        Assertions.assertEquals(item.getName(), itemNew.getName());
        Assertions.assertEquals(item.getDescription(), itemNew.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemNew.getAvailable());
        Assertions.assertEquals(item.getOwner(), itemNew.getOwner());
        Assertions.assertEquals(item.getRequest(), itemNew.getRequest());
    }
}

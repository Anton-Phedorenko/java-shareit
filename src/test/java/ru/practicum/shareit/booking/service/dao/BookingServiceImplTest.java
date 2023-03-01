package ru.practicum.shareit.booking.service.dao;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreation;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private User booker;

    private User owner;

    private Item item;

    private Booking booking;

    private BookingDtoCreation bookingDtoInput;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService);
        booker = new User();
        booker.setId(1L);
        booker.setName("Макс");
        booker.setEmail("max@yandex.ru");

        owner = new User();
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
        booking.setEnd(LocalDateTime.of(2022, 12, 10, 8, 0));
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        bookingDtoInput = new BookingDtoCreation();
        bookingDtoInput.setId(booking.getId());
        bookingDtoInput.setStart(booking.getStart());
        bookingDtoInput.setEnd(booking.getEnd());
        bookingDtoInput.setItemId(item.getId());
    }

    @Test
    void createTest() {

        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(booker);
        Mockito
                .when(itemService.getItemById(item.getId()))
                .thenReturn(item);
        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);


        BookingDto bookingDtoOutput = bookingService.create(bookingDtoInput, booker.getId());

        Assertions.assertEquals(booking.getId(), bookingDtoOutput.getId());
        Assertions.assertEquals(booking.getStart(), bookingDtoOutput.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDtoOutput.getEnd());
        Assertions.assertEquals(booking.getItem().getId(), bookingDtoOutput.getItem().getId());
        Assertions.assertEquals(booking.getItem().getName(), bookingDtoOutput.getItem().getName());
        Assertions.assertEquals(booking.getStatus(), bookingDtoOutput.getStatus());
        Assertions.assertEquals(booking.getBooker().getId(), bookingDtoOutput.getBooker().getId());
    }

    @Test
    void createOwnerTest() {

        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(owner);
        Mockito
                .when(itemService.getItemById(item.getId()))
                .thenReturn(item);

        BookingStatusException ex = Assertions.assertThrows(BookingStatusException.class, () -> {
            bookingService.create(bookingDtoInput, owner.getId());
        });
        Assertions.assertEquals("Нет необходимости заказывать у самого себя", ex.getMessage());
    }

    @Test
    void createAvalableFalseTest() {

        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(owner);
        Mockito
                .when(itemService.getItemById(item.getId()))
                .thenReturn(item);

        bookingDtoInput.setStart(LocalDateTime.of(2022, 12, 11, 8, 0));
        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () -> {
            bookingService.create(bookingDtoInput, booker.getId());
        });
        Assertions.assertEquals("Эту вещь арендовать нельзя", ex.getMessage());
    }

    @Test
    void updateTest() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.update(booking.getId(), owner.getId(), true);
        Assertions.assertEquals(booking.getId(), bookingDtoOutput.getId());
        Assertions.assertEquals(booking.getStart(), bookingDtoOutput.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDtoOutput.getEnd());
        Assertions.assertEquals(booking.getItem().getId(), bookingDtoOutput.getItem().getId());
        Assertions.assertEquals(booking.getItem().getName(), bookingDtoOutput.getItem().getName());
        Assertions.assertEquals(booking.getStatus(), bookingDtoOutput.getStatus());
        Assertions.assertEquals(booking.getBooker().getId(), bookingDtoOutput.getBooker().getId());
    }

    @Test
    void updateBookerTest() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        BookingStatusException ex = Assertions.assertThrows(BookingStatusException.class, () -> {
            bookingService.update(bookingDtoInput.getId(), booker.getId(), true);
        });
        Assertions.assertEquals("Заказчик не может изменить статус заказа!", ex.getMessage());
    }

    @Test
    void updateOwnerTrueTest() {
        booking.setStatus(Status.APPROVED);
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        BadRequestException ex = Assertions.assertThrows(BadRequestException.class, () -> {
            bookingService.update(bookingDtoInput.getId(), owner.getId(), true);
        });
        Assertions.assertEquals("У заказа уже такой статус", ex.getMessage());
    }

    @Test
    void getByIdTest() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        Assertions.assertEquals(booking.getId(), bookingDtoOutput.getId());
        Assertions.assertEquals(booking.getStart(), bookingDtoOutput.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDtoOutput.getEnd());
        Assertions.assertEquals(booking.getItem().getId(), bookingDtoOutput.getItem().getId());
        Assertions.assertEquals(booking.getItem().getName(), bookingDtoOutput.getItem().getName());
        Assertions.assertEquals(booking.getStatus(), bookingDtoOutput.getStatus());
        Assertions.assertEquals(booking.getBooker().getId(), bookingDtoOutput.getBooker().getId());
    }

    @Test
    void getByIdOtherUserTest() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getById(booking.getId(), 100L);
                });
        Assertions.assertEquals("Заказ не имеет отношения к данному пользователю", ex.getMessage());
    }

    @Test
    void getAllByOwnerTest() {
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByOwnerAll(owner.getId(),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByOwner(owner.getId(), "ALL", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getCurrentByOwnerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 8, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 20, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByOwnerCurrent(owner.getId(),
                        LocalDateTime.now().withNano(0),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByOwner(owner.getId(), "CURRENT", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getPastByOwnerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 8, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 10, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByOwnerPast(owner.getId(),
                        LocalDateTime.now().withNano(0),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByOwner(owner.getId(), "PAST", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getFutureByOwnerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 15, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 16, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByOwnerFuture(owner.getId(),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByOwner(owner.getId(), "FUTURE", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getWaitingByOwnerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 10, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 16, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByOwnerWaiting(owner.getId(),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByOwner(owner.getId(), "WAITING", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getRejectedByOwnerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 10, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 16, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByOwnerRejected(owner.getId(),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByOwner(owner.getId(), "REJECTED", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getAllByBookerTest() {
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByBookerAll(booker.getId(),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByBooker(booker.getId(), "ALL", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getCurrentByBookerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 8, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 20, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByBookerCurrent(booker.getId(),
                        LocalDateTime.now().withNano(0),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByBooker(booker.getId(), "CURRENT", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getPastByBookerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 8, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 10, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByBookerPast(booker.getId(),
                        LocalDateTime.now().withNano(0),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByBooker(booker.getId(), "PAST", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getFutureByBookerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 15, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 16, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByBookerFuture(booker.getId(),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByBooker(booker.getId(), "FUTURE", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getWaitingByBookerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 10, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 16, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByBookerWaiting(booker.getId(),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByBooker(booker.getId(), "WAITING", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getRejectedByBookerTest() {
        booking.setStart(LocalDateTime.of(2022, 12, 10, 8, 1));
        booking.setEnd(LocalDateTime.of(2022, 12, 16, 8, 1));
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDtoOutput = bookingService.getById(booking.getId(), booker.getId());
        bookingDtoOutputList.add(bookingDtoOutput);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        Mockito
                .when(bookingRepository.findAllByBookerRejected(booker.getId(),
                        PageRequest.of(0, 1, Sort.by(DESC, "start"))))
                .thenReturn(bookings);
        List<BookingDto> newBookingDtoOutputList =
                bookingService.getAllByBooker(booker.getId(), "REJECTED", 0, 1);

        Assertions.assertEquals(bookingDtoOutputList.get(0).getId(),
                newBookingDtoOutputList.get(0).getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStart(),
                newBookingDtoOutputList.get(0).getStart());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getEnd(),
                newBookingDtoOutputList.get(0).getEnd());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getId(),
                newBookingDtoOutputList.get(0).getItem().getId());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getItem().getName(),
                newBookingDtoOutputList.get(0).getItem().getName());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getStatus(),
                newBookingDtoOutputList.get(0).getStatus());
        Assertions.assertEquals(bookingDtoOutputList.get(0).getBooker().getId(),
                newBookingDtoOutputList.get(0).getBooker().getId());
    }

    @Test
    void getFailByBookerTest() {

        WrongStateException ex = Assertions.assertThrows(WrongStateException.class, () -> {
            bookingService.getAllByBooker(booker.getId(), "LIKE", 0, 1);
        });

        Assertions.assertEquals("Unknown state: LIKE", ex.getMessage());
    }


}


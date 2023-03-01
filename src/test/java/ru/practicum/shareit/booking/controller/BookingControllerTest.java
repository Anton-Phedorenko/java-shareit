package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreation;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private User booker;

    private User owner;

    private Booking booking;

    private BookingDtoCreation bookingDtoInput;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        booker.setName("Макс");
        booker.setEmail("max@yandex.ru");

        owner = new User();
        owner.setId(2L);
        owner.setName("Антон");
        owner.setEmail("anton@yandex.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Ракетка");
        item.setAvailable(true);
        item.setDescription("Теннисная ракетка");

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 3, 15, 8, 0, 1));
        booking.setEnd(LocalDateTime.of(2023, 3, 16, 8, 0, 1));
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
    void create() throws Exception {
        BookingDto bookingDtoOutput = BookingMapper.toBookingDto(booking);
        Mockito
                .when(bookingService.create(bookingDtoInput, booker.getId()))
                .thenReturn(bookingDtoOutput);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOutput.getItem().getName())));
    }

    @Test
    void update() throws Exception {
        BookingDto bookingDtoOutput = BookingMapper.toBookingDto(booking);
        bookingDtoOutput.setStatus(Status.APPROVED);
        Mockito
                .when(bookingService.update(bookingDtoInput.getId(), booker.getId(), true))
                .thenReturn(bookingDtoOutput);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOutput.getItem().getName())));
    }

    @Test
    void getById() throws Exception {
        BookingDto bookingDtoOutput = BookingMapper.toBookingDto(booking);
        Mockito
                .when(bookingService.getById(bookingDtoInput.getId(), booker.getId()))
                .thenReturn(bookingDtoOutput);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDtoOutput.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOutput.getItem().getName())));
    }

    @Test
    void getAllByOwner() throws Exception {
        BookingDto bookingDtoOutput = BookingMapper.toBookingDto(booking);
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        bookingDtoOutputList.add(bookingDtoOutput);
        Mockito
                .when(bookingService.getAllByOwner(owner.getId(), "ALL", 0, 1))
                .thenReturn(bookingDtoOutputList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoOutput.getStatus().name())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoOutput.getItem().getName())));
    }

    @Test
    void getAllByBooker() throws Exception {
        BookingDto bookingDtoOutput = BookingMapper.toBookingDto(booking);
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        bookingDtoOutputList.add(bookingDtoOutput);
        Mockito
                .when(bookingService.getAllByBooker(booker.getId(), "ALL", 0, 1))
                .thenReturn(bookingDtoOutputList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtoOutput.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDtoOutput.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoOutput.getStatus().name())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoOutput.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoOutput.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoOutput.getItem().getName())));
    }

    @Test
    void getAllByBookerFail() throws Exception {

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
    }
}

package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreation;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.valid.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody @Validated(Create.class) BookingDtoCreation bookingDtoCreation) {
        return bookingService.create(bookingDtoCreation, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId, @RequestParam Boolean approved,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable("bookingId") Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getAllByBooker(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PositiveOrZero @RequestParam(value = "from",
                                                   defaultValue = "0") Integer from,
                                           @Positive @RequestParam(value = "size",
                                                   defaultValue = "20") Integer size) {
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                          @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PositiveOrZero @RequestParam(value = "from",
                                                  defaultValue = "0") Integer from,
                                          @Positive @RequestParam(value = "size",
                                                  defaultValue = "20") Integer size) {
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}
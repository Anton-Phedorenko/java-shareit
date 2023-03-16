package ru.practicum.shareit.booking.service.dal;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingDtoOutput create(BookingDtoInput bookingDto, Long userId);

    BookingDtoOutput update(Long bookingId, Long userId, Boolean approved);

    BookingDtoOutput getById(Long bookingId, Long userId);

    List<BookingDtoOutput> getAllByBooker(Long bookerId, String state, Integer from, Integer size);

    List<BookingDtoOutput> getAllByOwner(Long ownerId, String state, Integer from, Integer size);

    Booking getByIdForBooking(Long bookingId);
}

package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreation;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoCreation bookingDtoCreation, Long userId);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> findByOwner(Long ownerId, String state);

    List<BookingDto> getAllByBooker(Long userId, String state);
}

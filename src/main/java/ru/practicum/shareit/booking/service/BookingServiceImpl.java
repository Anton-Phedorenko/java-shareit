package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreation;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.booking.Status.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final UserService userService;

    private final ItemService itemService;

    private final Sort sort = Sort.by(DESC, "start");

    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    @Transactional
    public BookingDto create(BookingDtoCreation bookingDtoCreation, Long userId) {
        User user = userService.getById(userId);
        Item item = itemService.getItemById(bookingDtoCreation.getItemId());
        if (!item.getOwner().getId().equals(userId)) {
            Booking booking = toBooking(bookingDtoCreation);
            booking.setBooker(user);
            booking.setItem(item);
            booking.setStatus(WAITING);
            if (booking.getEnd().isBefore(booking.getStart()) || !item.getAvailable()) {
                throw new BadRequestException("Эту вещь арендовать нельзя");
            }

            return toBookingDto(bookingRepository.save(booking));
        } else {
            throw new BookingStatusException("Нет необходимости заказывать у самого себя");
        }
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Заказа с id " + bookingId + " не существует"));
        if (!booking.getBooker().getId().equals(userId)) {
            if (approved && !booking.getStatus().equals(APPROVED)) {
                booking.setStatus(APPROVED);
            } else if (!approved && !booking.getStatus().equals(REJECTED)) {
                booking.setStatus(REJECTED);
            } else {
                throw new BadRequestException("У заказа уже такой статус");
            }
        } else {
            throw new BookingStatusException("Заказчик не может изменить статус заказа!");
        }

        return getById(bookingId, userId);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого заказа не существует"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Заказ не имеет отношения к данному пользователю");
        }

        return toBookingDto(booking);
    }


    @Override
    public List<BookingDto> getAllByBooker(Long userId, String state, Integer from, Integer size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(DESC, "start"));
        List<Booking> bookings = List.of();
        switch (State.States.getState(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBookerAll(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerCurrent(userId, LocalDateTime.now().withNano(0),
                        pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerPast(userId, LocalDateTime.now().withNano(0),
                        pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerFuture(userId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerWaiting(userId, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerRejected(userId, pageable);
                break;
        }
        return BookingMapper.bookingsDto(bookings);
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(DESC, "start"));
        List<Booking> bookings = List.of();
        switch (State.States.getState(state)) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerAll(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerCurrent(userId, LocalDateTime.now().withNano(0),
                        pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerPast(userId, LocalDateTime.now().withNano(0),
                        pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerFuture(userId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerWaiting(userId, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerRejected(userId, pageable);
                break;
        }
        return BookingMapper.bookingsDto(bookings);
    }
}


package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
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

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final UserService userService;

    private final ItemService itemService;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

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
            Booking booking = BookingMapper.toBooking(bookingDtoCreation);
            booking.setBooker(user);
            booking.setItem(item);
            booking.setStatus(Status.WAITING);
            if (booking.getEnd().isBefore(booking.getStart()) || !item.getAvailable()) {
                throw new BadRequestException("Эту вещь арендовать нельзя");
            }
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new BookingStatusException("Нет необходимости заказывать у самого себя");
        }
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого заказа не существует"));
        if (!booking.getBooker().getId().equals(userId)) {
            if (approved && !booking.getStatus().equals(Status.APPROVED)) {
                booking.setStatus(Status.APPROVED);
            } else if (!approved && !booking.getStatus().equals(Status.REJECTED)) {
                booking.setStatus(Status.REJECTED);
            } else {
                throw new BadRequestException("У заказа уже такой статус");
            }
        } else {
            throw new BookingStatusException("Заказчик не может изменить статус заказа!");
        }
        return findById(bookingId, userId);
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого заказа не существует"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Заказ не имеет отношения к данному пользователю");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findByOwner(Long ownerId, String state) {
        userService.getById(ownerId);
        List<Booking> bookings = List.of();
        switch (State.States.getState(state)) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerAll(ownerId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerCurrent(ownerId, LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerPast(ownerId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerFuture(ownerId, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerWaiting(ownerId, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerRejected(ownerId, sort);
                break;
        }

        return BookingMapper.bookingsDto(bookings);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, String state) {
        userService.getById(userId);
        List<Booking> bookings = List.of();
        switch (State.States.getState(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBookerAll(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerCurrent(userId, LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerPast(userId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerFuture(userId, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerWaiting(userId, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerRejected(userId, sort);
                break;
        }
        return BookingMapper.bookingsDto(bookings);
    }
}


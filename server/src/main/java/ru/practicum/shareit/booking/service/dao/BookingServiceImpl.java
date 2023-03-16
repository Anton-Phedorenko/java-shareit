package ru.practicum.shareit.booking.service.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.dal.BookingService;
import ru.practicum.shareit.booking.status.State;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exeption.exeptions.ObjectExcistenceException;
import ru.practicum.shareit.exeption.exeptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.dal.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.dal.UserService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDtoOutput create(BookingDtoInput bookingDto, Long userId) {
        User user = userService.getById(userId);
        Item item = itemService.getByIdForItem(bookingDto.getItemId());
        if (!item.getOwner().getId().equals(userId)) {
            Booking booking = BookingMapper.toBooking(bookingDto);
            booking.setBooker(user);
            booking.setItem(item);
            booking.setStatus(Status.WAITING);
            System.out.println();
            if (item.getAvailable() && !booking.getStart().isAfter(booking.getEnd())) {
                return getById(bookingRepository.save(booking).getId(), userId);
            } else {
                throw new ValidationException("Вещь не доступна для аренды");
            }
        } else {
            throw new ObjectExcistenceException("Пользователь является владельцем");
        }
    }

    @Override
    @Transactional
    public BookingDtoOutput update(Long bookingId, Long userId, Boolean approved) {
        userService.getById(userId);
        Booking booking = getByIdForBooking(bookingId);
        if (!userId.equals(booking.getBooker().getId())) {
            if (approved && !booking.getStatus().equals(Status.APPROVED)) {
                booking.setStatus(Status.APPROVED);
            } else if (!approved && !booking.getStatus().equals(Status.REJECTED)) {
                booking.setStatus(Status.REJECTED);
            } else {
                throw new ValidationException("Сделка имеет такой статус");
            }
            return getById(bookingId, userId);
        } else {
            throw new ObjectExcistenceException("Попытка изменения статуса сделки не владельцем вещи");
        }
    }

    @Override
    public BookingDtoOutput getById(Long bookingId, Long userId) {
        userService.getById(userId);
        Booking booking = getByIdForBooking(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectExcistenceException("Пользователь не относится к сделке");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDtoOutput> getAllByOwner(Long id, String state, Integer from, Integer size) {
        userService.getById(id);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(DESC, "start"));
        Clock clock = Clock.system(ZoneId.of("Europe/Moscow"));
        List<Booking> bookings = List.of();
        switch (State.States.getState(state)) {
            case ALL:
                bookings = bookingRepository.getAllByOwnerAll(id, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.getAllByOwnerCurrent(id,
                        LocalDateTime.now(clock).withNano(0),
                        pageable);
                break;
            case PAST:
                bookings = bookingRepository.getAllByOwnerPast(id,
                        LocalDateTime.now(clock).withNano(0),
                        pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.getAllByOwnerFuture(id, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.getAllByOwnerWaiting(id, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.getAllByOwnerRejected(id, pageable);
                break;
        }
        return BookingMapper.toBookingDtoList(bookings);
    }

    @Override
    public List<BookingDtoOutput> getAllByBooker(Long id, String state, Integer from, Integer size) {
        userService.getById(id);
        Clock clock = Clock.system(ZoneId.of("Europe/Moscow"));
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(DESC, "start"));
        List<Booking> bookings = List.of();
        switch (State.States.getState(state)) {
            case ALL:
                bookings = bookingRepository.getAllByBookerAll(id, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.getAllByBookerCurrent(id,
                        LocalDateTime.now(clock).withNano(0),
                        pageable);
                break;
            case PAST:
                bookings = bookingRepository.getAllByBookerPast(id,
                        LocalDateTime.now(clock).withNano(0),
                        pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.getAllByBookerFuture(id, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.getAllByBookerWaiting(id, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.getAllByBookerRejected(id, pageable);
                break;
        }
        return BookingMapper.toBookingDtoList(bookings);
    }

    @Override
    public Booking getByIdForBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectExcistenceException("Инструмент не сущестует"));
    }
}

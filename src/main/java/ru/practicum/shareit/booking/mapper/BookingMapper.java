package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreation;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {
    public static Booking toBooking(BookingDtoCreation bookingDtoCreation) {
        Booking booking = new Booking();
        booking.setId(booking.getId());
        booking.setStart(bookingDtoCreation.getStart());
        booking.setEnd(bookingDtoCreation.getEnd());

        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()),
                new BookingDto.Booker(booking.getBooker().getId())
        );
    }

    public static List<BookingDto> bookingsDto(List<Booking> bookings) {
        return bookings.stream().map(b -> toBookingDto(b)).collect(Collectors.toList());
    }
}

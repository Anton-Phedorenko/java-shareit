package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status = Status.WAITING;
    private Item item;
    private Booker booker;

    @Data
    @AllArgsConstructor
    public static class Booker {
        private Long id;
    }

    @Data
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }
}

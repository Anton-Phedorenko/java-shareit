package ru.practicum.shareit.booking;

import java.time.LocalDate;
import java.util.Objects;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Long itemId;
    private Long bookerId;
    private Status status;

    public Booking() {
    }

    public Booking(Long id, LocalDate start, LocalDate end, Long itemId, Long bookerId, Status status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.bookerId = bookerId;
        this.status = status;
    }

    public Booking(LocalDate start, LocalDate end, Long itemId, Long bookerId, Status status) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.bookerId = bookerId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getBookerId() {
        return bookerId;
    }

    public void setBookerId(Long bookerId) {
        this.bookerId = bookerId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(start, booking.start) && Objects.equals(end, booking.end) && Objects.equals(itemId, booking.itemId) && Objects.equals(bookerId, booking.bookerId) && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, itemId, bookerId, status);
    }
}

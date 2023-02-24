package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1")
    List<Booking> findAllByOwnerAll(Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 AND ?2 BETWEEN b.start AND b.end")
    List<Booking> findAllByOwnerCurrent(Long ownerId, LocalDateTime time, Sort sort);

    @Query("SELECT b FROM Booking b JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 AND (b.status = 'APPROVED' OR b.status = 'WAITING')")
    List<Booking> findAllByOwnerFuture(Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 AND b.status = 'APPROVED' AND b.end < ?2")
    List<Booking> findAllByOwnerPast(Long ownerId, LocalDateTime time, Sort sort);

    @Query("SELECT b FROM Booking b JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 AND b.status = 'WAITING'")
    List<Booking> findAllByOwnerWaiting(Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = ?1 AND (b.status = 'REJECTED' OR b.status = 'CANCELED')")
    List<Booking> findAllByOwnerRejected(Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1")
    List<Booking> findAllByBookerAll(Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 AND ?2 BETWEEN b.start AND b.end")
    List<Booking> findAllByBookerCurrent(Long bookerId, LocalDateTime time, Sort sort);

    @Query("SELECT b FROM Booking b JOIN Item i ON i.id = b.item.id " +
            "WHERE b.booker.id = ?1 AND (b.status = 'APPROVED' OR b.status = 'WAITING')")
    List<Booking> findAllByBookerFuture(Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.status = 'APPROVED' AND b.end < ?2")
    List<Booking> findAllByBookerPast(Long bookerId, LocalDateTime time, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and b.status = 'WAITING'")
    List<Booking> findAllByBookerWaiting(Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 AND (b.status = 'REJECTED' OR b.status = 'CANCELED')")
    List<Booking> findAllByBookerRejected(Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.booker.id <> ?2 AND b.item.available = TRUE")
    List<Booking> findBookingByItemId(Long itemId, Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b")
    List<Booking> findAll(Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.status = 'APPROVED' AND b.item IN ?1 ")
    List<Booking> findApprovedForItems(List<Item> items, Sort sort);
}

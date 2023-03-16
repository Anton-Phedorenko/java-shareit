package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b join Item i on i.id = b.item.id " +
            "where i.owner.id = ?1")
    List<Booking> getAllByOwnerAll(Long ownerId, Pageable pageable);

    @Query("select b from Booking b join Item i on i.id = b.item.id " +
            "where i.owner.id = ?1 and ?2 BETWEEN b.start and b.end")
    List<Booking> getAllByOwnerCurrent(Long ownerId, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b join Item i on i.id = b.item.id " +
            "where i.owner.id = ?1 and (b.status = 'APPROVED' or b.status = 'WAITING')")
    List<Booking> getAllByOwnerFuture(Long ownerId, Pageable pageable);

    @Query("select b from Booking b join Item i on i.id = b.item.id " +
            "where i.owner.id = ?1 and b.status = 'APPROVED' and b.end < ?2")
    List<Booking> getAllByOwnerPast(Long ownerId, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b join Item i on i.id = b.item.id " +
            "where i.owner.id = ?1 and b.status = 'WAITING'")
    List<Booking> getAllByOwnerWaiting(Long ownerId, Pageable pageable);

    @Query("select b from Booking b join Item i on i.id = b.item.id " +
            "where i.owner.id = ?1 and (b.status = 'REJECTED' or b.status = 'CANCELED')")
    List<Booking> getAllByOwnerRejected(Long ownerId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1")
    List<Booking> getAllByBookerAll(Long bookerId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and ?2 BETWEEN b.start and b.end")
    List<Booking> getAllByBookerCurrent(Long bookerId, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b join Item i on i.id = b.item.id " +
            "where b.booker.id = ?1 and (b.status = 'APPROVED' or b.status = 'WAITING')")
    List<Booking> getAllByBookerFuture(Long bookerId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and b.status = 'APPROVED' and b.end < ?2")
    List<Booking> getAllByBookerPast(Long bookerId, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and b.status = 'WAITING'")
    List<Booking> getAllByBookerWaiting(Long bookerId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and (b.status = 'REJECTED' or b.status = 'CANCELED')")
    List<Booking> getAllByBookerRejected(Long bookerId, Pageable pageable);

    @Query("select b from Booking b")
    List<Booking> findAll(Sort sort);

    @Query("select b from Booking b where b.status = 'APPROVED' " +
            "and b.item in ?1 ")
    List<Booking> findApprovedForItems(List<Item> items, Sort sort);
}

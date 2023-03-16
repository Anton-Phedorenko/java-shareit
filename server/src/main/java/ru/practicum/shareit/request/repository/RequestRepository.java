package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select ir from ItemRequest ir where ir.requestor.id = ?1")
    List<ItemRequest> getAllByUser(Long userId, Sort sort);

    @Query("select ir from ItemRequest ir where ir.requestor.id <> ?1")
    List<ItemRequest> getAllWithSize(Long userId, Pageable pageable);
}

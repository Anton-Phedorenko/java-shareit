package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.owner.id = ?1 ")
    List<Item> getAll(Long id, Pageable pageable);


    @Query("select i from Item i where (lower(i.description) like concat('%', ?1, '%') " +
            "or lower(i.name) like concat('%', ?1, '%')) and i.available = true ")
    List<Item> getByText(String text, Pageable pageable);

    @Query("select i from Item i where i.request IN ?1")
    List<Item> getByRequestId(List<ItemRequest> requests);
}

package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE (LOWER(i.description) LIKE CONCAT('%', ?1, '%') " +
            "OR LOWER(i.name) LIKE CONCAT('%', ?1, '%')) AND i.available = TRUE ")
    List<Item> findByText(String text);

    @Query("SELECT i FROM Item i WHERE i.owner.id = ?1 " +
            " ORDER BY i.id")
    List<Item> getAll(Long id);
}


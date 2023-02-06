package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    ItemDto create(Item item);

    ItemDto update(Item item,Long id);

    void delete(Long id);

    Optional<ItemDto> findById(Long id);

    List<ItemDto> findAll(Long ownerId);
}

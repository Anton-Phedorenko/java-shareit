package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long id);

    void delete(Long id);

    Item findItemById(Long id);

    ItemDto findById(Long itemId, Long userId);

    List<ItemDto> findByOwnerId(Long ownerId);
}

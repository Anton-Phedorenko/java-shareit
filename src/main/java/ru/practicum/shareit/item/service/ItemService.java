package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long id);

    void delete(Long id);

    Item getItemById(Long id);

    ItemDto getById(Long itemId, Long userId);

    List<ItemDto> getByOwnerId(Long ownerId);
}

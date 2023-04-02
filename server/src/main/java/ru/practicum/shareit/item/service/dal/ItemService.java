package ru.practicum.shareit.item.service.dal;

import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDtoOutput create(ItemDtoInput itemDto, Long userId);

    ItemDtoOutput update(ItemDtoInput itemDto, Long userId);

    ItemDtoOutput getById(Long itemId, Long ownerId);

    List<ItemDtoOutput> getAll(Long userId, Integer from, Integer size);

    List<ItemDtoOutput> getByText(String text, Integer from, Integer size);

    Item getByIdForItem(Long itemId);
}

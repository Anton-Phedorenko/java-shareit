package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    private static Long itemId = 0L;

    private final Map<Long, ItemDto> items = new HashMap<>();

    @Override
    public ItemDto create(Item item) {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setId(++itemId);
        items.put(itemDto.getId(), itemDto);
        return itemDto;
    }

    @Override
    public ItemDto update(Item item, Long id) {
        ItemDto itemDto = items.get(id);
        itemDto.setName(item.getName() != null ? item.getName() : itemDto.getName());
        itemDto.setDescription(item.getDescription() != null ? item.getDescription() : itemDto.getDescription());
        itemDto.setAvailable(item.getAvailable() != null ? item.getAvailable() : itemDto.getAvailable());
        itemDto.setOwnerId(item.getOwnerId() != null ? item.getOwnerId() : itemDto.getOwnerId());
        itemDto.setRequestId(item.getRequestId() != null ? item.getRequestId() : itemDto.getRequestId());
        return itemDto;
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public ItemDto findById(Long id) {
        return items.get(id);
    }

    @Override
    public List<ItemDto> findAll(Long ownerId) {
        return items.values().stream().filter(i -> i.getOwnerId().equals(ownerId)).collect(Collectors.toList());
    }


    public List<ItemDto> findByText(String text, Long ownerId) {
        if (!text.isEmpty())
            return items.values().stream()
                    .filter(i -> (i.getName() + i.getDescription()).trim().toLowerCase().contains(text))
                    .filter(i->i.getAvailable().equals(true)).collect(Collectors.toList());
        else return Collections.emptyList();
    }
}

package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDtoOutput toItemDto(Item item) {
        Long itemRequestId = null;
        if (item.getRequest() != null) {
            itemRequestId = item.getRequest().getId();
        }
        return ItemDtoOutput.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(itemRequestId)
                .build();
    }

    public static Item toItem(ItemDtoInput itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static List<ItemDtoOutput> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static ItemRequestDtoOutput.Item toItemRequestDtoItem(Item item) {
        ItemRequestDtoOutput.Item itemNew = new ItemRequestDtoOutput.Item();
        itemNew.setId(item.getId());
        itemNew.setName(item.getName());
        itemNew.setDescription(item.getDescription());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setRequestId(item.getRequest().getId());
        return itemNew;
    }

    public static List<ItemRequestDtoOutput.Item> toItemRequestDtoItemList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemRequestDtoItem)
                .collect(Collectors.toList());
    }

}

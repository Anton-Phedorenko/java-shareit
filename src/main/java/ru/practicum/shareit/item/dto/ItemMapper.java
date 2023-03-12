package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        Long itemRequestId = null;
        if (item.getRequest() != null) {
            itemRequestId = item.getRequest().getId();
        }
        return ItemDto.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(itemRequestId)
                .build();
    }

    public static List<ItemDto> itemsDto(List<Item> items) {
        return items.stream().map(i -> ItemMapper.toItemDto(i)).collect(Collectors.toList());
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemRequestDto.Item toItemRequestDtoItem(Item item) {
        ItemRequestDto.Item itemNew = new ItemRequestDto.Item();
        itemNew.setId(item.getId());
        itemNew.setName(item.getName());
        itemNew.setDescription(item.getDescription());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setRequestId(item.getRequest().getId());
        return itemNew;
    }

    public static List<ItemRequestDto.Item> toItemRequestDtoItemList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemRequestDtoItem)
                .collect(Collectors.toList());
    }
}

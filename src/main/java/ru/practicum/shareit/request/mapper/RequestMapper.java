package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestCreation;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public class RequestMapper {
    public static ItemRequestDto toRequestDto(ItemRequest request) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(request.getId());
        itemRequestDto.setDescription(request.getDescription());
        itemRequestDto.setCreated(request.getCreated());
        return itemRequestDto;
    }

    public static ItemRequest toRequest(ItemRequestCreation itemRequestCreation) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestCreation.getDescription());
        itemRequest.setCreated(itemRequestCreation.getCreated());
        return itemRequest;
    }
}

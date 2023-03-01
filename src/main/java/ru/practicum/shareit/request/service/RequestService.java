package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreation;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto create(ItemRequestCreation itemRequestCreation, Long userId);

    ItemRequestDto getById(Long id, Long userId);

    List<ItemRequestDto> getAllByUserId(Long userId);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);
}

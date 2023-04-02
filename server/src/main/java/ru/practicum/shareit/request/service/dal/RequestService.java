package ru.practicum.shareit.request.service.dal;

import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;

import java.util.List;

public interface RequestService {

    ItemRequestDtoOutput create(ItemRequestDtoInput itemRequestDto, Long userId);

    ItemRequestDtoOutput getById(Long requestId, Long userId);

    List<ItemRequestDtoOutput> getAllByUserId(Long userId);

    List<ItemRequestDtoOutput> getAll(Long userId, Integer from, Integer size);

}

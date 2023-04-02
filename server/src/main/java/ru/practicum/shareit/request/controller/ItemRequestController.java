package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.service.dal.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final RequestService requestService;
    public static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDtoOutput create(@RequestHeader(USER_ID) Long userId,
                                       @RequestBody ItemRequestDtoInput requestDto) {
        return requestService.create(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> getAllByUserId(@RequestHeader(USER_ID) Long userId) {
        return requestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> getAll(@RequestHeader(USER_ID) Long userId,
                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return requestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOutput getById(@RequestHeader(USER_ID) Long userId,
                                        @PathVariable("requestId") Long requestId) {
        return requestService.getById(requestId, userId);
    }
}

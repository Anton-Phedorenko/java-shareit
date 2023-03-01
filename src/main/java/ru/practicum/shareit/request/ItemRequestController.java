package ru.practicum.shareit.request;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.valid.Create;
import ru.practicum.shareit.request.dto.ItemRequestCreation;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final RequestService requestService;

    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping()
    public ItemRequestDto create(@RequestBody @Validated(Create.class) ItemRequestCreation itemRequestCreation, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.create(itemRequestCreation, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                       @Positive @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return requestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("requestId") Long requestId) {
        return requestService.getById(requestId, userId);
    }
}
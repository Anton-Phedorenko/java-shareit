package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestBody @Valid Item item, @RequestHeader(value = "X-SHARER-USER-ID") Long ownerId) {
        item.setOwnerId(ownerId);
        return itemService.create(item);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody @Valid Item item, @PathVariable Long id, @RequestHeader(value = "X-SHARER-USER-ID") Long ownerId) {
        item.setOwnerId(ownerId);
        return itemService.update(item, id);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id) {
        if(id<0||id==null) throw new BadRequestException("Некорректный id");
        return itemService.findById(id);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(value = "X-SHARER-USER-ID") Long ownerId) {
        return itemService.findAll(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text, @RequestHeader(value = "X-SHARER-USER-ID") Long ownerId) {
        return itemService.findByText(text, ownerId);
    }
}

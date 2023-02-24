package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.valid.Create;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    private final CommentService commentService;

    public ItemController(ItemServiceImpl itemService, CommentService commentService) {
        this.itemService = itemService;
        this.commentService = commentService;
    }

    @PostMapping
    public ItemDto create(@RequestBody @Validated(Create.class) ItemDto itemDto, @RequestHeader(value = "X-SHARER-USER-ID") Long ownerId) {
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody @Valid ItemDto itemDto, @PathVariable Long id, @RequestHeader(value = "X-SHARER-USER-ID") Long ownerId) {
        itemDto.setId(id);

        return itemService.update(itemDto, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        if (id == null || id < 0) {
            throw new BadRequestException("Некорректный id");
        }
        return itemService.getById(id, ownerId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(value = "X-SHARER-USER-ID") Long ownerId) {
        return itemService.getByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text) {
        if (text.isEmpty()) {
            return List.of();
        }

        return itemService.getByText(text.trim().toLowerCase());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@Valid @RequestBody CommentDto commentDto, @PathVariable("itemId") Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return commentService.create(commentDto, itemId, userId);
    }
}

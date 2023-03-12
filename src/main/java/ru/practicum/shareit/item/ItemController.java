package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.valid.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    private final CommentService commentService;

    public ItemController(ItemService itemService, CommentService commentService) {
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


    @GetMapping("/search")
    public List<ItemDto> getByText(@RequestParam("text") String text,
                                   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                   @Positive @RequestParam(value = "size", defaultValue = "20") Integer size) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemService.getByText(text.toLowerCase(), from, size);
        }
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-SHARER-USER-ID") Long userId,
                                @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                @Positive @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return itemService.getAll(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@Valid @RequestBody CommentDto commentDto, @PathVariable("itemId") Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return commentService.create(commentDto, itemId, userId);
    }
}

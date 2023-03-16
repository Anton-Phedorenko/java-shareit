package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.service.dal.CommentService;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.service.dal.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;
    public static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDtoOutput create(@RequestBody ItemDtoInput itemDto,
                                @RequestHeader(USER_ID) Long userId) {
        return itemService
                .create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOutput update(@RequestBody ItemDtoInput itemDto,
                                @RequestHeader(USER_ID) Long userId,
                                @PathVariable("itemId") Long itemId) {
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOutput getById(@PathVariable("itemId") Long itemId,
                                 @RequestHeader(USER_ID) Long ownerId) {
        return itemService.getById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDtoOutput> getAll(@RequestHeader(USER_ID) Long userId,
                                      @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDtoOutput> getByText(@RequestParam("text") String text,
                                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @RequestParam(value = "size", defaultValue = "20") Integer size) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemService.getByText(text.toLowerCase(), from, size);
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable("itemId") Long itemId,
                                    @RequestHeader(USER_ID) Long userId) {
        return commentService.create(commentDto, itemId, userId);
    }
}

package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.client.CommentClient;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.user.valid.Create;
import ru.practicum.shareit.user.valid.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final CommentClient commentClient;
    public static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(Create.class) ItemDtoInput itemDto,
                                         @RequestHeader(USER_ID) Long userId) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody @Validated(Update.class) ItemDtoInput itemDto,
                                         @RequestHeader(USER_ID) Long userId,
                                         @PathVariable("itemId") Long itemId) {
        itemDto.setId(itemId);
        return itemClient.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable("itemId") Long itemId,
                                          @RequestHeader(USER_ID) Long ownerId) {
        return itemClient.getById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID) Long userId,
                                         @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByText(@RequestParam("text") String text,
                                            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @Positive @RequestParam(value = "size", defaultValue = "20") Integer size) {
        if (!text.isBlank()) {
            return itemClient.getByText(text.toLowerCase(), from, size);
        } else {
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable("itemId") Long itemId,
                                                @RequestHeader(USER_ID) Long userId) {
        return commentClient.create(itemId, userId, commentDto);
    }
}

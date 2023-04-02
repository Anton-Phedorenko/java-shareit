package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.valid.Create;
import ru.practicum.shareit.user.valid.Update;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(Create.class) UserDto userDto) {
        log.info("Пользователь создан");
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") @Positive Long userId,
                                         @RequestBody @Validated(Update.class) UserDto userDto) {
        userDto.setId(userId);
        log.info("Пользователь обновился");
        return userClient.update(userDto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long userId) {
        log.info("Пользователь с id = {}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Вывод всех созданных пользователей");
        return userClient.getAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long userId) {
        log.info("Удаление пользователя с id = {}", userId);
        userClient.deleteById(userId);
    }
}

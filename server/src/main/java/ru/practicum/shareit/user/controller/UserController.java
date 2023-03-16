package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.dal.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Пользователь создан");
        return UserMapper.toUserDto(userService.create(UserMapper.toUser(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId,
                          @RequestBody UserDto userDto) {

        log.info("Пользователь обновился");
        userDto.setId(userId);
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDto)));
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") Long userId) {
        log.info("Пользователь с id = {}", userId);
        return UserMapper.toUserDto(userService.getById(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Вывод всех созданных пользователей");
        return UserMapper.toListUserDto(userService.getAll());
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long userId) {
        log.info("Удаление пользователя с id = {}", userId);
        userService.deleteById(userId);
    }
}

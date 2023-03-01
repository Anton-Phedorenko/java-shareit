package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.valid.Create;
import ru.practicum.shareit.item.valid.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.*;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody @Validated(Create.class) UserDto userDto) {
        User user = userService.create(toUser(userDto));

        return toUserDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody @Validated(Update.class) UserDto userDto, @PathVariable Long id) {
        if (id == null || id < 0) {
            throw new BadRequestException("Некорректный id пользователя");
        }
        userDto.setId(id);

        return toUserDto(userService.update(toUser(userDto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        if (id == null || id < 0) {
            throw new BadRequestException("Некорректный id");
        }

        return toUserDto(userService.getById(id));
    }

    @GetMapping
    public List<UserDto> findAll() {
        return usersDto(userService.getAll());
    }
}

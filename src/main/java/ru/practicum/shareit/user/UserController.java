package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    public UserController(UserServiceImpl userService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        return UserMapper.toUserDto(userService.create(UserMapper.toUser(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long id) {
        if (id == null || id < 0) throw new BadRequestException("Некорректный id пользователя");
        userDto.setId(id);
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        if (id < 0 || id == null) throw new BadRequestException("Некорректный id");
        return UserMapper.toUserDto(userService.getById(id));
    }

    @GetMapping
    public List<UserDto> findAll() {
        return UserMapper.usersDto(userService.getAll());
    }
}

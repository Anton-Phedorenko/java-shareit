package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> findUserByEmail(String email);
}

package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

public interface UserService extends UserDao {
    Optional<UserDto> findUserByEmail(String email);
}

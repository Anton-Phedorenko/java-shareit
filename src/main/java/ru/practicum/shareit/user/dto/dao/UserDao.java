package ru.practicum.shareit.user.dto.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    UserDto create(User user);

    UserDto update(User user, Long id);

    void delete(Long id);

    Optional<UserDto> findById(Long id);

    List<UserDto> findAll();
}

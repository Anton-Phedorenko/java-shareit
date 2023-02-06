package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    UserDto create(User user);

    UserDto update(User user, Long id);

    void delete(Long id);

    Optional<UserDto> findById(Long id);

    List<UserDto> findAll();
}

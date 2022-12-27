package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserDao {
    UserDto create(User user);

    UserDto update(User user, Long id);

    void delete(Long id);

    UserDto findById(Long id);

    List<UserDto> findAll();
}

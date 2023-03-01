package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
//    Optional<User> findUserByEmail(String email);

    User create(User user);

    User update(User user);

    void delete(Long id);

    User getById(Long id);

    List<User> getAll();
}

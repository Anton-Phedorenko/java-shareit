package ru.practicum.shareit.user.service.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.exeptions.ObjectExcistenceException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.dal.UserService;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        return updateUserIfParamIsNull(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectExcistenceException("Пользователь не сущестует"));
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        userRepository.findById(userId)
                .ifPresent(user -> userRepository.deleteById(user.getId()));
    }

    private User updateUserIfParamIsNull(User user) {
        User userNew = getById(user.getId());
        if (user.getEmail() != null) {
            userNew.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            userNew.setName(user.getName());
        }
        return userNew;
    }
}

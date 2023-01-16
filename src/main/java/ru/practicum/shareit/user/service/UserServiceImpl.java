package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDaoImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserDaoImpl userDao;

    @Autowired
    public UserServiceImpl(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    public UserDto create(User user) {
        if (findUserByEmail(user.getEmail()).isPresent())
            throw new EmailConflictException("Пользователь с таким email уже существует");
        valid(user);
        return userDao.create(user);
    }

    public UserDto update(User user, Long id) {
        if (findUserByEmail(user.getEmail()).isPresent())
            throw new EmailConflictException("Обновлениеи не внесет изменений");
        return userDao.update(user, id);
    }

    public void delete(Long id) {
        userDao.delete(id);
    }

    public UserDto findById(Long id) {
        return userDao.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public List<UserDto> findAll() {
        return userDao.findAll();
    }

    public void valid(User user) {
        if (user.getEmail().isEmpty() || user.getEmail() == null) throw new RuntimeException();
    }

    @Override
    public Optional<UserDto> findUserByEmail(String email) {
        return Optional.ofNullable(findAll()
                .stream()
                .filter(userDto -> userDto.getEmail().equals(email))
                .findFirst().orElse(null));
    }
}

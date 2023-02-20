package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public User create(User user) {
        valid(user);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User update(User user) {
        return updateUserIfExist(user);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void valid(User user) {
        if (user.getEmail().isEmpty() || user.getEmail() == null) throw new RuntimeException();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(getAll()
                .stream()
                .filter(userDto -> userDto.getEmail().equals(email))
                .findFirst().orElse(null));
    }

    private User updateUserIfExist(User user) {
        User updateUser = getById(user.getId());
        if (user.getEmail() != null) updateUser.setEmail(user.getEmail());
        if (user.getName() != null) updateUser.setName(user.getName());
        userRepository.save(updateUser);
        return updateUser;
    }
}

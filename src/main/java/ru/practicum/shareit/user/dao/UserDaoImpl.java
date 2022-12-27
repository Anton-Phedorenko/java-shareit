package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserDaoImpl implements UserDao {

    private static Long userId = 0L;
    private final Map<Long, UserDto> users = new HashMap<>();


    @Override
    public UserDto create(User user) {
        UserDto userDto = UserMapper.toUserDto(user);
        userDto.setId(++userId);
        users.put(userDto.getId(), userDto);
        return userDto;
    }

    @Override
    public UserDto update(User user, Long id) {
        UserDto updateUser = users.get(id);
        updateUser.setName(user.getName() != null ? user.getName() : updateUser.getName());
        updateUser.setEmail(user.getEmail() != null ? user.getEmail() : updateUser.getEmail());
        users.put(updateUser.getId(), updateUser);
        return updateUser;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public UserDto findById(Long id) {
        return id < 1 || id == null ? null : users.get(id);
    }

    @Override
    public List<UserDto> findAll() {
        return users.values().stream().collect(Collectors.toList());
    }



    public Map<Long, UserDto> getUsers() {
        return users;
    }
}

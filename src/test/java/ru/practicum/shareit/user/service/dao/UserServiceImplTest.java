package ru.practicum.shareit.user.service.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Макс");
        user.setEmail("max@yandex.ru");

        Mockito
                .when(userRepository.save(user))
                .thenReturn(user);

        User newUser = userService.create(user);
        Assertions.assertEquals(1L, newUser.getId());
        Assertions.assertEquals("Макс", newUser.getName());
        Assertions.assertEquals("max@yandex.ru", newUser.getEmail());
    }

    @Test
    void updateTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Макс");
        user.setEmail("max@yandex.ru");

        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("Максим");
        newUser.setEmail("max@yandex.ru");

        User userResult = userService.update(newUser);
        Assertions.assertEquals(1L, userResult.getId());
        Assertions.assertEquals("Максим", userResult.getName());
        Assertions.assertEquals("max@yandex.ru", userResult.getEmail());
    }

    @Test
    void getAllTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Макс");
        user.setEmail("max@yandex.ru");

        List<User> users = new ArrayList<>();
        users.add(user);

        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);

        List<User> allUsers = userService.getAll();
        Assertions.assertEquals(users, allUsers, "Списки не одиннаковы");
    }

    @Test
    void getByIdTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Макс");
        user.setEmail("max@yandex.ru");

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User newUser = userService.getById(user.getId());
        Assertions.assertEquals(1L, newUser.getId());
        Assertions.assertEquals("Макс", newUser.getName());
        Assertions.assertEquals("max@yandex.ru", newUser.getEmail());
    }


}

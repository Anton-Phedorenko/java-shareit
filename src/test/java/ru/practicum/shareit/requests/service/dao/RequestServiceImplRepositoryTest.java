package ru.practicum.shareit.requests.service.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
class RequestServiceImplRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;
    private User booker;
    private User owner;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setName("Макс");
        booker.setEmail("max@yandex.ru");
        userRepository.save(booker);

        owner = new User();
        owner.setName("Антон");
        owner.setEmail("anton@yandex.ru");
        userRepository.save(owner);

        itemRequest = new ItemRequest();
        itemRequest.setRequestor(booker);
        itemRequest.setDescription("Нужна ракетка");
        itemRequest.setCreated(LocalDateTime.of(2022, 12, 9, 12, 0, 1));
        requestRepository.save(itemRequest);
    }

    @Test
    void getAllByUser() {
        List<ItemRequest> itemRequests = requestRepository.getAllByUser(booker.getId(),
                Sort.by(DESC, "created"));

        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest.getId(), itemRequests.get(0).getId());
    }

    @Test
    void getAllWithSize() {
        List<ItemRequest> itemRequests = requestRepository.getAllWithSize(owner.getId(),
                PageRequest.of(0, 1, Sort.by(DESC, "created")));

        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest.getId(), itemRequests.get(0).getId());
    }
}

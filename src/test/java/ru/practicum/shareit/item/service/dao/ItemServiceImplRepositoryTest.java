package ru.practicum.shareit.item.service.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemServiceImplRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

    private User owner;

    private Item item;

    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        User booker = new User();
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

        item = new Item();
        item.setOwner(owner);
        item.setName("Ракетка");
        item.setAvailable(true);
        item.setDescription("Теннисная ракетка");
        item.setRequest(itemRequest);
        itemRepository.save(item);
    }

    @Test
    void getAll() {
        List<Item> items = itemRepository.getAll(owner.getId(), PageRequest.of(0, 1, Sort.unsorted()));
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void getByText() {
        List<Item> items = itemRepository.findByText("ракетка", PageRequest.of(0, 1, Sort.unsorted()));

        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void getByRequestId() {
        List<ItemRequest> itemRequests = requestRepository.findAll();
        List<Item> items = itemRepository.getByRequestId(itemRequests);

        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }
}

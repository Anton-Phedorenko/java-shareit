package ru.practicum.shareit.requests.service.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreation;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    private RequestService requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    private User requestor;
    private Item item;
    private ItemRequest request;
    private ItemRequestCreation itemRequestDtoInput;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(requestRepository, userService, itemRepository);
        requestor = new User();
        requestor.setId(1L);
        requestor.setName("Макс");
        requestor.setEmail("max@yandex.ru");

        User owner = new User();
        owner.setId(2L);
        owner.setName("Антон");
        owner.setEmail("anton@yandex.ru");

        request = new ItemRequest();
        request.setId(1L);
        request.setCreated(LocalDateTime.of(2022, 12, 7, 8, 0));
        request.setDescription("Хочу теннисную ракетку");
        request.setRequestor(requestor);

        item = new Item();
        item.setId(1L);
        item.setName("Ракетка");
        item.setAvailable(true);
        item.setDescription("Теннисная ракетка");
        item.setRequest(request);

        itemRequestDtoInput = new ItemRequestCreation();
        itemRequestDtoInput.setId(request.getId());
        itemRequestDtoInput.setDescription(request.getDescription());
        itemRequestDtoInput.setCreated(request.getCreated());
    }

    @Test
    void create() {
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(requestor);
        Mockito
                .when(requestRepository.save(any()))
                .thenReturn(request);

        ItemRequestDto itemRequestDtoOutput = requestService.create(itemRequestDtoInput, requestor.getId());
        Assertions.assertEquals(request.getId(), itemRequestDtoOutput.getId());
        Assertions.assertEquals(request.getCreated(), itemRequestDtoOutput.getCreated());
        Assertions.assertEquals(request.getDescription(), itemRequestDtoOutput.getDescription());
    }

    @Test
    void getById() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(request);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(requestor);
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.getByRequestId(itemRequests))
                .thenReturn(items);

        ItemRequestDto itemRequestDtoOutput = requestService.getById(request.getId(), requestor.getId());
        Assertions.assertEquals(request.getId(), itemRequestDtoOutput.getId());
        Assertions.assertEquals(request.getCreated(), itemRequestDtoOutput.getCreated());
        Assertions.assertEquals(request.getDescription(), itemRequestDtoOutput.getDescription());
        Assertions.assertEquals(item.getId(), itemRequestDtoOutput.getItems().get(0).getId());
        Assertions.assertEquals(item.getRequest().getId(), itemRequestDtoOutput.getItems().get(0).getRequestId());
        Assertions.assertEquals(item.getName(), itemRequestDtoOutput.getItems().get(0).getName());
        Assertions.assertEquals(item.getAvailable(), itemRequestDtoOutput.getItems().get(0).getAvailable());
    }

    @Test
    void getAllByUserId() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(request);
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(requestor);
        Mockito
                .when(requestRepository.getAllByUser(requestor.getId(), Sort.by(DESC, "created")))
                .thenReturn(itemRequests);

        List<ItemRequestDto> itemRequestDtoOutputList = requestService.getAllByUserId(requestor.getId());
        Assertions.assertEquals(request.getId(), itemRequestDtoOutputList.get(0).getId());
        Assertions.assertEquals(request.getCreated(), itemRequestDtoOutputList.get(0).getCreated());
        Assertions.assertEquals(request.getDescription(), itemRequestDtoOutputList.get(0).getDescription());
    }

    @Test
    void getAll() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(request);
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(requestor);
        Mockito
                .when(requestRepository.getAllWithSize(requestor.getId(),
                        PageRequest.of(0, 1, Sort.by(DESC, "created"))))
                .thenReturn(itemRequests);
        List<ItemRequestDto> itemRequestDtoOutputList = requestService.getAll(requestor.getId(), 0, 1);
        Assertions.assertEquals(request.getId(), itemRequestDtoOutputList.get(0).getId());
        Assertions.assertEquals(request.getCreated(), itemRequestDtoOutputList.get(0).getCreated());
        Assertions.assertEquals(request.getDescription(), itemRequestDtoOutputList.get(0).getDescription());
    }
}

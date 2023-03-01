package ru.practicum.shareit.requests.service.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreation;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTestIntegration {

    private final EntityManager em;

    private final RequestService requestService;

    private final UserService userService;

    private User requestorNew;

    private ItemRequestCreation itemRequestDtoInput;

    @BeforeEach
    void setUp() {
        User requestor = new User();
        requestor.setName("Антон");
        requestor.setEmail("anton@yandex.ru");
        requestorNew = userService.create(requestor);

        itemRequestDtoInput = new ItemRequestCreation();
        itemRequestDtoInput.setRequestorId(requestorNew.getId());
        itemRequestDtoInput.setDescription("ракетка для настольного тенниса");
    }

    @Test
    void create() {
        ItemRequestDto itemRequestDtoOutput = requestService.create(itemRequestDtoInput, requestorNew.getId());

        TypedQuery<ItemRequest> query = em.createQuery("select ir from ItemRequest ir where ir.id = :id",
                ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestDtoOutput.getId())
                .getSingleResult();
        assertThat(itemRequest.getId(), notNullValue());
        assertEquals(itemRequestDtoInput.getRequestorId(), itemRequest.getRequestor().getId());
        assertEquals(itemRequestDtoInput.getDescription(), itemRequest.getDescription());
    }

    @Test
    void getById() {
        ItemRequestDto itemRequestDtoOutput = requestService.create(itemRequestDtoInput, requestorNew.getId());
        ItemRequestDto itemRequestDtoOutput1 = requestService.getById(itemRequestDtoOutput.getId(),
                requestorNew.getId());

        assertEquals(itemRequestDtoOutput.getId(), itemRequestDtoOutput1.getId());
        assertEquals(itemRequestDtoOutput.getDescription(), itemRequestDtoOutput1.getDescription());
        assertEquals(itemRequestDtoOutput.getCreated(), itemRequestDtoOutput1.getCreated());
    }

    @Test
    void getAllByUserId() {
        ItemRequestDto itemRequestDtoOutput = requestService.create(itemRequestDtoInput, requestorNew.getId());
        List<ItemRequestDto> itemRequestDtoOutputList = requestService.getAllByUserId(requestorNew.getId());

        assertEquals(itemRequestDtoOutput.getId(), itemRequestDtoOutputList.get(0).getId());
        assertEquals(itemRequestDtoOutput.getDescription(), itemRequestDtoOutputList.get(0).getDescription());
        assertEquals(itemRequestDtoOutput.getCreated(), itemRequestDtoOutputList.get(0).getCreated());
    }

    @Test
    void getAll() {
        User user = new User();
        user.setEmail("profi@yandex.ru");
        user.setName("Profi");
        ItemRequestDto itemRequestDtoOutput = requestService.create(itemRequestDtoInput, requestorNew.getId());
        List<ItemRequestDto> itemRequestDtoOutputList = requestService
                .getAll(userService.create(user).getId(), 0, 1);

        assertEquals(itemRequestDtoOutput.getId(), itemRequestDtoOutputList.get(0).getId());
        assertEquals(itemRequestDtoOutput.getDescription(), itemRequestDtoOutputList.get(0).getDescription());
        assertEquals(itemRequestDtoOutput.getCreated(), itemRequestDtoOutputList.get(0).getCreated());
    }
}

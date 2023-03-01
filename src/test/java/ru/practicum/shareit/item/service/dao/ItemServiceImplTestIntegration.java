package ru.practicum.shareit.item.service.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTestIntegration {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private User ownerNew;
    private ItemDto itemDtoInput;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Антон");
        owner.setEmail("anton@yandex.ru");
        ownerNew = userService.create(owner);

        itemDtoInput = new ItemDto();
        itemDtoInput.setName("Ракетка");
        itemDtoInput.setDescription("Теннисная ракетка");
        itemDtoInput.setAvailable(true);
    }

    @Test
    void createTest() {
        ItemDto itemDtoOutput = itemService.create(itemDtoInput, ownerNew.getId());

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoOutput.getId())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertEquals(itemDtoInput.getName(), item.getName());
        assertEquals(itemDtoInput.getDescription(), item.getDescription());
        assertEquals(itemDtoInput.getAvailable(), item.getAvailable());
    }

    @Test
    void updateTest() {
        ItemDto itemDtoInputNew = new ItemDto();
        Long id = itemService.create(itemDtoInput, ownerNew.getId()).getId();
        itemDtoInputNew.setId(id);
        itemDtoInputNew.setName("Ракетка");
        itemDtoInputNew.setDescription("Теннисная ракетка, для профессионалов");
        itemDtoInputNew.setAvailable(true);
        itemService.update(itemDtoInputNew, ownerNew.getId());

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoInputNew.getId())
                .getSingleResult();

        assertEquals(id, item.getId());
        assertEquals(itemDtoInputNew.getName(), item.getName());
        assertEquals(itemDtoInputNew.getDescription(), item.getDescription());
        assertEquals(itemDtoInputNew.getAvailable(), item.getAvailable());
    }

    @Test
    void getByIdTest() {
        ItemDto itemDto = itemService.create(itemDtoInput, ownerNew.getId());
        ItemDto itemDtoOutput = itemService.getById(itemDto.getId(), ownerNew.getId());

        assertEquals(itemDto.getName(), itemDtoOutput.getName());
        assertEquals(itemDto.getDescription(), itemDtoOutput.getDescription());
        assertEquals(itemDto.getAvailable(), itemDtoOutput.getAvailable());
        assertEquals(itemDto.getRequestId(), itemDtoOutput.getRequestId());
    }

    @Test
    void getAllTest() {
        List<ItemDto> itemDtoOutputList = new ArrayList<>();
        itemDtoOutputList.add(itemService.create(itemDtoInput, ownerNew.getId()));

        List<ItemDto> itemDtoOutputListNew = itemService.getAll(ownerNew.getId(), 0, 1);
        assertEquals(1, itemDtoOutputListNew.size());
        assertEquals(itemDtoOutputList.get(0).getName(), itemDtoOutputListNew.get(0).getName());
        assertEquals(itemDtoOutputList.get(0).getDescription(), itemDtoOutputListNew.get(0).getDescription());
        assertEquals(itemDtoOutputList.get(0).getAvailable(), itemDtoOutputListNew.get(0).getAvailable());
        assertEquals(itemDtoOutputList.get(0).getRequestId(), itemDtoOutputListNew.get(0).getRequestId());
    }

    @Test
    void getByTextTest() {
        List<ItemDto> itemDtoOutputList = new ArrayList<>();
        itemDtoOutputList.add(itemService.create(itemDtoInput, ownerNew.getId()));

        List<ItemDto> itemDtoOutputListNew = itemService.getByText("ракетка", 0, 1);
        assertEquals(1, itemDtoOutputListNew.size());
        assertEquals(itemDtoOutputList.get(0).getName(), itemDtoOutputListNew.get(0).getName());
        assertEquals(itemDtoOutputList.get(0).getDescription(), itemDtoOutputListNew.get(0).getDescription());
        assertEquals(itemDtoOutputList.get(0).getAvailable(), itemDtoOutputListNew.get(0).getAvailable());
        assertEquals(itemDtoOutputList.get(0).getRequestId(), itemDtoOutputListNew.get(0).getRequestId());
    }

    @Test
    void getByIdForItemTest() {
        ItemDto itemDto = itemService.create(itemDtoInput, ownerNew.getId());
        Item item = itemService.getItemById(itemDto.getId());

        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }
}

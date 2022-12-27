package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDaoImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDaoImpl;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDaoImpl itemDao;
    private final UserDaoImpl userDao;

    public ItemServiceImpl(ItemDaoImpl itemDao, UserDaoImpl userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public ItemDto create(Item item) {
        valid(item);
        return itemDao.create(item);
    }

    @Override
    public ItemDto update(Item item, Long id) {
        ItemDto updateItem = itemDao.findById(id);
        if (item.getOwnerId() == null || item.getOwnerId() < 0)
            throw new BadRequestException("Нельзя определить владельца вещи");
        if (!item.getOwnerId().equals(updateItem.getOwnerId()))
            throw new NotFoundException("Нельзя переопрделеить владельца вещи");

        return itemDao.update(item, id);
    }

    @Override
    public void delete(Long id) {
        itemDao.delete(id);
    }

    @Override
    public ItemDto findById(Long id) {
        if (id < 0 || id == null) throw new RuntimeException();
        return itemDao.findById(id);
    }

    @Override
    public List<ItemDto> findAll(Long ownerId) {
        return itemDao.findAll(ownerId);
    }

    public void valid(Item item) {
        userDao.getUsers().keySet()
                .stream()
                .filter(ownerId -> item.getOwnerId()
                        .equals(ownerId))
                .findFirst().orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        if (item.getName() == null || item.getName().isBlank())
            throw new BadRequestException("Ошибка в поле name");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new BadRequestException("Ошибка в поле description");
        if (item.getAvailable() == null || item.getAvailable().toString().isBlank())
            throw new BadRequestException("Ошибка в поле available");
    }

    public List<ItemDto> findByText(String text, Long ownerId) {
        text = text.trim().toLowerCase();
        return itemDao.findByText(text, ownerId);
    }
}

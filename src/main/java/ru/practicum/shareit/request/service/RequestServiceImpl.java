package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreation;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemRequestDtoItemList;
import static ru.practicum.shareit.request.mapper.RequestMapper.toRequest;
import static ru.practicum.shareit.request.mapper.RequestMapper.toRequestDto;

@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestRepository;

    private UserService userService;

    private ItemRepository itemRepository;


    public RequestServiceImpl(RequestRepository requestRepository, UserService userService, ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestCreation itemRequestCreation, Long userId) {
        ItemRequest itemRequest = toRequest(itemRequestCreation);
        itemRequest.setRequestor(userService.getById(userId));
        return toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getById(Long id, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запроса с id " + id + " не существует"));
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);
        Map<ItemRequest, List<Item>> answers = getAnswer(requests);
        return appendItemToRequest(itemRequest,
                answers.getOrDefault(itemRequest, Collections.emptyList()));
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        userService.getById(userId);
        return getWithItems(requestRepository.getAllByUser(userId, Sort.by(DESC, "created")));
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        userService.getById(userId);
        return getWithItems(requestRepository.getAllWithSize(userId, PageRequest.of(from, size, Sort.by(DESC, "created"))));
    }

    private Map<ItemRequest, List<Item>> getAnswer(List<ItemRequest> requests) {
        return itemRepository.getByRequestId(requests)
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));
    }

    private ItemRequestDto appendItemToRequest(ItemRequest itemRequest, List<Item> items) {
        ItemRequestDto itemRequestDto = toRequestDto(itemRequest);
        itemRequestDto.setItems(toItemRequestDtoItemList(items));
        return itemRequestDto;
    }

    private List<ItemRequestDto> getWithItems(List<ItemRequest> requests) {
        Map<ItemRequest, List<Item>> answers = getAnswer(requests);
        List<ItemRequestDto> itemRequestDtoOutputList = new ArrayList<>();
        for (ItemRequest itemRequest : requests) {
            itemRequestDtoOutputList.add(appendItemToRequest(itemRequest,
                    answers.getOrDefault(itemRequest, Collections.emptyList())));
        }
        return itemRequestDtoOutputList;
    }
}
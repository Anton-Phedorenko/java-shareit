package ru.practicum.shareit.request.service.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.exeptions.ObjectExcistenceException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.dal.RequestService;
import ru.practicum.shareit.user.service.dal.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDtoOutput create(ItemRequestDtoInput itemRequestDto, Long userId) {
        ItemRequest itemRequest = RequestMapper.toRequest(itemRequestDto);
        itemRequest.setRequestor(userService.getById(userId));
        return RequestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDtoOutput getById(Long requestId, Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectExcistenceException("Такого запроса нет"));
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);
        Map<ItemRequest, List<Item>> answers = getAnswer(requests);
        return appendItemToItemRequest(itemRequest,
                answers.getOrDefault(itemRequest, Collections.emptyList()));
    }

    @Override
    public List<ItemRequestDtoOutput> getAllByUserId(Long userId) {
        userService.getById(userId);
        return getWithItems(requestRepository.getAllByUser(userId, Sort.by(DESC, "created")));
    }

    @Override
    public List<ItemRequestDtoOutput> getAll(Long userId, Integer from, Integer size) {
        userService.getById(userId);
        return getWithItems(requestRepository
                .getAllWithSize(userId, PageRequest.of(from, size, Sort.by(DESC, "created"))));
    }

    private Map<ItemRequest, List<Item>> getAnswer(List<ItemRequest> requests) {
        return itemRepository.getByRequestId(requests)
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));
    }

    private ItemRequestDtoOutput appendItemToItemRequest(ItemRequest itemRequest, List<Item> items) {
        ItemRequestDtoOutput itemRequestDtoOutput = RequestMapper.toRequestDto(itemRequest);
        itemRequestDtoOutput.setItems(ItemMapper.toItemRequestDtoItemList(items));
        return itemRequestDtoOutput;
    }

    private List<ItemRequestDtoOutput> getWithItems(List<ItemRequest> requests) {
        Map<ItemRequest, List<Item>> answers = getAnswer(requests);
        return requests.stream()
                .map(itemRequest -> appendItemToItemRequest(itemRequest,
                        answers.getOrDefault(itemRequest, Collections.emptyList())))
                .collect(toList());
    }
}

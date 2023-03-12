package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final RequestRepository requestRepository;


    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userService.getById(userId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto));
        item.setOwner(user);
        appendRequest(itemDto, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long userId) {

        userService.getById(userId);
        Item updateItem = getItemById(itemDto.getId());
        Item newItem = ItemMapper.toItem(itemDto);
        if (updateItem.getOwner().getId().equals(userId)) {
            newItem = partialUpdate(appendRequest(itemDto, newItem));
            return getById(newItem.getId(), newItem.getOwner().getId());
        }
        throw new NotFoundException("У данной вещи другой владелец");
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public ItemDto getById(Long itemId, Long ownerId) {
        List<Item> items = new ArrayList<>();
        items.add(getItemById(itemId));
        Map<Item, List<Booking>> approveBookings = getApprovedBookings(items);
        Map<Item, List<Comment>> comments = getComments(items);
        if (items.get(0).getOwner().getId().equals(ownerId)) {
            return appendCommentsToItem(appendBookingToItem(items.get(0),
                            approveBookings.getOrDefault(items.get(0), Collections.emptyList())),
                    comments.getOrDefault(items.get(0), Collections.emptyList()));
        }
        return appendCommentsToItem(ItemMapper.toItemDto(items.get(0)),
                comments.getOrDefault(items.get(0), Collections.emptyList()));
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Вещь не может быть найдена"));
    }

    public ItemDto appendCommentsToItem(ItemDto itemDto, List<Comment> comments) {
        itemDto.setComments(CommentMapper.toListItemCommentDto(comments));
        return itemDto;
    }

    @Override
    public List<ItemDto> getByText(String text, Integer from, Integer size) {
        return ItemMapper.itemsDto(itemRepository.findByText(text, PageRequest.of(from > 0 ? from / size : 0,
                size, Sort.unsorted())));
    }

    @Override
    public List<ItemDto> getAll(Long userId, Integer from, Integer size) {
        userService.getById(userId);
        List<Item> items = itemRepository.getAll(userId, PageRequest.of(from > 0 ? from / size : 0,
                size, Sort.unsorted()));
        Map<Item, List<Booking>> approvedBookings = getApprovedBookings(items);
        Map<Item, List<Comment>> comments = getComments(items);
        List<ItemDto> itemDtoOutputList = new ArrayList<>();
        for (Item item : items) {
            itemDtoOutputList.add(appendCommentsToItem(appendBookingToItem(item,
                            approvedBookings.getOrDefault(item, Collections.emptyList())),
                    comments.getOrDefault(item, Collections.emptyList())));
        }
        return itemDtoOutputList;
    }


    public Item partialUpdate(Item item) {
        Item itemNew = getItemById(item.getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            itemNew.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemNew.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemNew.setAvailable(item.getAvailable());
        }
        if (item.getRequest() != null) {
            itemNew.setRequest(item.getRequest());
        }
        return itemNew;
    }

    private Map<Item, List<Booking>> getApprovedBookings(List<Item> items) {
        return bookingRepository.findApprovedForItems(
                        items, Sort.by(DESC, "start"))
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));
    }

    private Map<Item, List<Comment>> getComments(List<Item> items) {
        return commentRepository.findCommentForItems(items)
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
    }


    public ItemDto appendBookingToItem(Item item, List<Booking> bookings) {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookings.stream()
                .filter(b -> ((b.getEnd().isEqual(now) || b.getEnd().isBefore(now))
                        || (b.getStart().isEqual(now) || b.getStart().isBefore(now))))
                .findFirst()
                .orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .reduce((first, second) -> second)
                .orElse(null);
        ItemDto.Booking lastBookingNew = new ItemDto.Booking();
        ItemDto.Booking nextBookingNew = new ItemDto.Booking();
        if (lastBooking != null) {
            lastBookingNew.setId(lastBooking.getId());
            lastBookingNew.setBookerId(lastBooking.getBooker().getId());
            itemDto.setLastBooking(lastBookingNew);
        }
        if (nextBooking != null) {
            nextBookingNew.setId(nextBooking.getId());
            nextBookingNew.setBookerId(nextBooking.getBooker().getId());
            itemDto.setNextBooking(nextBookingNew);
        }

        return itemDto;
    }

    private Item appendRequest(ItemDto itemDto, Item itemNew) {
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Такого запроса не существует"));
            itemNew.setRequest(itemRequest);
        }
        return itemNew;
    }
}


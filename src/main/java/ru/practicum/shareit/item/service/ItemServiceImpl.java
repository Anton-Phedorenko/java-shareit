package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
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
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository, UserService userService, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userService.findById(userId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto));
        item.setOwner(user);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long userId) {
        if (itemDto.getId() == null) throw new NotFoundException("Вещь не может быть найдена");
        Item updateItem = findItemById(itemDto.getId());
        Item newItem = ItemMapper.toItem(itemDto);
        System.out.println(updateItem);
        if (updateItem.getOwner().getId().equals(userId)) {
            newItem = partialUpdate(newItem);
            return ItemMapper.toItemDto(newItem);
        }
        throw new NotFoundException("У данной вещи другой владелец");
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public ItemDto findById(Long itemId, Long ownerId) {
        List<Item> items = new ArrayList<>();
        items.add(findItemById(itemId));
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

    public Item findItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Вещь не может быть найдена"));
    }

    public ItemDto appendCommentsToItem(ItemDto itemDto, List<Comment> comments) {
        itemDto.setComments(CommentMapper.toListItemCommentDto(comments));
        return itemDto;
    }


    @Override
    public List<ItemDto> findByOwnerId(Long ownerId) {
        List<ItemDto> itemDtos = new ArrayList<>();
        List<Item> items = itemRepository.getAll(ownerId);
        Map<Item, List<Booking>> approvedBookings = getApprovedBookings(items);
        for (Item item : items) {
            itemDtos.add(appendBookingToItem(item,
                    approvedBookings.getOrDefault(item, Collections.emptyList())));
        }
        return itemDtos;
    }

    public List<ItemDto> findByText(String text) {
        return ItemMapper.itemsDto(itemRepository.getByText(text));
    }

    public Item partialUpdate(Item item) {
        Item itemNew = findItemById(item.getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            itemNew.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemNew.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemNew.setAvailable(item.getAvailable());
        }
        if (item.getRequestId() != null) {
            itemNew.setRequestId(item.getRequestId());
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
}


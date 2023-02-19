package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.valid.Create;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotEmpty(groups = Create.class)
    private String name;
    @NotEmpty(groups = Create.class)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;

    public ItemDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Data
    public static class Booking {
        private Long id;
        private Long bookerId;
    }

    @Data
    @AllArgsConstructor
    public static class Comment {
        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }
}

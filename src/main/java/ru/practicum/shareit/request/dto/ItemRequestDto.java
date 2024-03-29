package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;

    private String description;

    private LocalDateTime created;

    private List<Item> items;

    @Data
    @NoArgsConstructor
    public static class Item {
        private Long id;

        private String name;

        private String description;

        private Boolean available;

        private Long requestId;
    }
}

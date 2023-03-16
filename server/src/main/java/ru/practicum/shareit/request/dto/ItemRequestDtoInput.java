package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ItemRequestDtoInput {

    private Long id;
    private Long requestorId;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}
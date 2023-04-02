package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemRequestDtoInput {

    private Long id;
    private Long requestorId;
    private String description;
}
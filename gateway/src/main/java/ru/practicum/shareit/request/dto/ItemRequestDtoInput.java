package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.valid.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ItemRequestDtoInput {

    private Long id;
    private Long requestorId;
    @NotBlank(groups = {Create.class})
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}
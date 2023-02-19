package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.valid.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoCreation {
    private Long id;
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    private Long itemId;
}

package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jsonOutput;

    @Autowired
    private JacksonTester<List<BookingDto>> jsonList;

    @Test
    void testItemDto() throws Exception {
        BookingDto bookingDtoOutput = new BookingDto();
        bookingDtoOutput.setId(1L);
        bookingDtoOutput.setStart(LocalDateTime.of(2023, 3, 8, 8, 0, 1));
        bookingDtoOutput.setEnd(LocalDateTime.of(2023, 3, 10, 8, 0, 1));
        bookingDtoOutput.setStatus(Status.APPROVED);
        bookingDtoOutput.setItem(new BookingDto.Item(1L, "Ракетка"));
        bookingDtoOutput.setBooker(new BookingDto.Booker(1L));

        JsonContent<BookingDto> result = jsonOutput.write(bookingDtoOutput);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Ракетка");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoOutput.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoOutput.getEnd().toString());
    }

    @Test
    void testItemDtoFromJsonList() throws Exception {
        List<BookingDto> bookingDtoOutputList = new ArrayList<>();
        BookingDto bookingDtoOutput = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 3, 8, 8, 0, 1))
                .end(LocalDateTime.of(2023, 3, 10, 8, 0, 1))
                .status(Status.APPROVED)
                .item(new BookingDto.Item(1L, "Ракетка"))
                .booker(new BookingDto.Booker(1L))
                .build();
        bookingDtoOutputList.add(bookingDtoOutput);

        JsonContent<List<BookingDto>> result = jsonList.write(bookingDtoOutputList);

        assertThat(result).extractingJsonPathNumberValue("$[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$[0].item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$[0].item.name").isEqualTo("Ракетка");
        assertThat(result).extractingJsonPathStringValue("$[0].status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$[0].booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$[0].start")
                .isEqualTo(bookingDtoOutput.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$[0].end")
                .isEqualTo(bookingDtoOutput.getEnd().toString());
    }
}


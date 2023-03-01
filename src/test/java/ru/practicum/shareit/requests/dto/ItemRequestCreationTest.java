package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.request.dto.ItemRequestCreation;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class ItemRequestCreationTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jsonOutput;
    @Autowired
    private JacksonTester<ItemRequestCreation> jsonInput;

    @Test
    void testItemDtoFromJson() throws Exception {
        ItemRequestDto itemRequestDtoOutput = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна ракетка для тенниса")
                .items(List.of())
                .created(LocalDateTime.of(2022, 12, 8, 8, 0, 1))
                .build();

        JsonContent<ItemRequestDto> result = jsonOutput.write(itemRequestDtoOutput);
        ObjectContent<ItemRequestCreation> requestDtoObjectContent = jsonInput.parse(result.getJson());

        assertEquals(itemRequestDtoOutput.getId(), requestDtoObjectContent.getObject().getId());
        assertEquals(itemRequestDtoOutput.getDescription(), requestDtoObjectContent.getObject().getDescription());
        assertEquals(itemRequestDtoOutput.getCreated(), requestDtoObjectContent.getObject().getCreated());

    }
}
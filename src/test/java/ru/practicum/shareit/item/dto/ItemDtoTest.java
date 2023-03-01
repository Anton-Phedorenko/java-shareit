package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jsonOutput;
    @Autowired
    private JacksonTester<ItemDto> jsonInput;

    @Test
    void testItemDtoFromJson() throws Exception {
        ItemDto itemDtoOutput = ItemDto.builder()
                .id(1L)
                .name("Ракетка")
                .description("Ракетка для тенниса")
                .available(true)
                .requestId(1L)
                .comments(List.of())
                .build();

        JsonContent<ItemDto> result = jsonOutput.write(itemDtoOutput);
        ObjectContent<ItemDto> itemDtoObjectContent = jsonInput.parse(result.getJson());

        assertEquals(itemDtoOutput.getId(), itemDtoObjectContent.getObject().getId());
        assertEquals(itemDtoOutput.getName(), itemDtoObjectContent.getObject().getName());
        assertEquals(itemDtoOutput.getDescription(), itemDtoObjectContent.getObject().getDescription());
        assertEquals(itemDtoOutput.getAvailable(), itemDtoObjectContent.getObject().getAvailable());
        assertEquals(itemDtoOutput.getRequestId(), itemDtoObjectContent.getObject().getRequestId());
    }
}

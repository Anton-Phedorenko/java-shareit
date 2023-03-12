package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jsonOutput;

    @Autowired
    private JacksonTester<List<ItemRequestDto>> jsonList;

    @Test
    void testItemDto() throws Exception {
        ItemRequestDto itemRequestDtoOutput = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна ракетка для тенниса")
                .items(List.of())
                .created(LocalDateTime.of(2022, 12, 8, 8, 0, 1))
                .build();

        JsonContent<ItemRequestDto> result = jsonOutput.write(itemRequestDtoOutput);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Нужна ракетка для тенниса");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(List.of());
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDtoOutput.getCreated().toString());
    }

    @Test
    void testItemDtoList() throws Exception {
        List<ItemRequestDto> itemRequestDtoOutputList = new ArrayList<>();
        ItemRequestDto.Item item = new ItemRequestDto.Item();
        item.setId(1L);
        item.setRequestId(1L);
        item.setAvailable(true);
        item.setName("Ракетка");
        item.setDescription("Ракетка для тенниса");
        List<ItemRequestDto.Item> items = new ArrayList<>();
        items.add(item);
        ItemRequestDto itemRequestDtoOutput = new ItemRequestDto();
        itemRequestDtoOutput.setId(1L);
        itemRequestDtoOutput.setDescription("Нужна ракетка для тенниса");
        itemRequestDtoOutput.setCreated(LocalDateTime
                .of(2022, 12, 8, 8, 0, 1));
        itemRequestDtoOutput.setItems(items);
        itemRequestDtoOutputList.add(itemRequestDtoOutput);

        JsonContent<List<ItemRequestDto>> result = jsonList.write(itemRequestDtoOutputList);

        assertThat(result).extractingJsonPathNumberValue("$[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$[0].description")
                .isEqualTo("Нужна ракетка для тенниса");
        assertThat(result).extractingJsonPathNumberValue("$[0].items.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$[0].items.[0].name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$[0].items.[0].description")
                .isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$[0].items.[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$[0].items.[0].requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$[0].created")
                .isEqualTo(itemRequestDtoOutput.getCreated().toString());
    }
}

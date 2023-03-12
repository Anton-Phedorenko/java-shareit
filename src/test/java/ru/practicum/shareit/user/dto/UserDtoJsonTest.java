package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "david.who@mail.ru",
                "David");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("David");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("david.who@mail.ru");
    }

    @Test
    void testUserDtoFromJson() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "david.who@mail.ru",
                "David");

        JsonContent<UserDto> result = json.write(userDto);
        ObjectContent<UserDto> userDtoObjectContent = json.parse(result.getJson());

        assertEquals(1L, userDtoObjectContent.getObject().getId());
        assertEquals("David", userDtoObjectContent.getObject().getName());
        assertEquals("david.who@mail.ru", userDtoObjectContent.getObject().getEmail());
    }
}
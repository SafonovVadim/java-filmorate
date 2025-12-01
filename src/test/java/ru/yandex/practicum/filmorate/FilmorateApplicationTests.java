package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn400WhenNameIsNull() throws Exception {
        String json = "{ \"description\": \"cool film\", \"releaseDate\": \"1999-01-01\", \"duration\": 120 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Название не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenNameIsEmpty() throws Exception {
        String json = "{ \"name\": \"\", \"description\": \"cool film\", \"releaseDate\": \"1999-01-01\", \"duration\": 120 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Название не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenDescriptionTooLong() throws Exception {
        String longDesc = "a".repeat(201);
        String json = String.format(" {\n" +
                "                  \"name\": \"Movie\",\n" +
                "                  \"description\": \"%s\",\n" +
                "                  \"releaseDate\": \"2000-01-01\",\n" +
                "                  \"duration\": 100\n" +
                "                }", longDesc);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Максимальная длина описания — 200 символов"));
    }

    @Test
    void shouldReturn400WhenReleaseDateTooEarly() throws Exception {
        String json = "{ \"name\": \"Old\", \"description\": \"old\", \"releaseDate\": \"1895-12-27\", \"duration\": 10 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.releaseDate").value("Дата релиза — не раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldReturn400WhenDurationIsZero() throws Exception {
        String json = "{ \"name\": \"Zero\", \"description\": \"zero\", \"releaseDate\": \"2000-01-01\", \"duration\": 0 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.duration").value("Продолжительность должна быть положительной"));
    }

    @Test
    void shouldReturn200WhenValidFilm() throws Exception {
        String json = "{ \"name\": \"Valid\", \"description\": \"good\", \"releaseDate\": \"1999-01-01\", \"duration\": 100 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Valid"));
    }

    @Test
    void shouldReturn400WhenLoginIsNull() throws Exception {
        String json = "{ \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.login").value("Логин не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenLoginIsEmpty() throws Exception {
        String json = "{ \"login\": \"\", \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.login").value("Логин не может быть пустым"));

    }

    @Test
    void shouldReturn400WhenLoginContainsSpaces() throws Exception {
        String json = "{ \"login\": \"user login\", \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.login").value("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void shouldReturn400WhenEmailIsNull() throws Exception {
        String json = "{ \"login\": \"user\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenEmailIsEmpty() throws Exception {
        String json = "{ \"login\": \"user\", \"email\": \"\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenEmailWithoutAt() throws Exception {
        String json = "{ \"login\": \"user\", \"email\": \"user-yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Неверный формат email"));
    }

    @Test
    void shouldReturn400WhenBirthdayInFuture() throws Exception {
        String json = "{ \"login\": \"user\", \"email\": \"user@yandex.ru\", \"birthday\": \"3000-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthday").value("Дата рождения не может быть в будущем"));
    }

    @Test
    void shouldCreateUserWithValidData() throws Exception {
        String json = "{ \"login\": \"user\", \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.email").value("user@yandex.ru"));
    }

    @Test
    void shouldSetNameAsLoginIfNameIsBlank() throws Exception {
        String json = "{ \"login\": \"user\", \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void shouldAcceptCustomName() throws Exception {
        String json = "{ \"login\": \"user\", \"name\": \"Иван\", \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван"));
    }

    @Test
    void shouldReturn404WhenUpdateNonExistingUser() throws Exception {
        String json = "{ \"id\": 999, \"login\": \"user\", \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateExistingUser() throws Exception {
        String createJson = "{ \"login\": \"user\", \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson));
        String updateJson = "{ \"id\": 1, \"login\": \"userUpdated\", \"email\": \"updated@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("userUpdated"))
                .andExpect(jsonPath("$.email").value("updated@yandex.ru"));
    }
}


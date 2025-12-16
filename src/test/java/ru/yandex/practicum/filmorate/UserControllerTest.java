package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() throws Exception {
        user1 = createUser("user1@yandex.ru", "user1", "User One", LocalDate.of(1990, 1, 1));
        user2 = createUser("user2@yandex.ru", "user2", "User Two", LocalDate.of(1990, 1, 2));
        user3 = createUser("user3@yandex.ru", "user3", "User Three", LocalDate.of(1990, 1, 3));
    }

    private User createUser(String email, String login, String name, LocalDate birthday) throws Exception {
        String json = String.format("{\n" +
                        "    \"email\": \"%s\",\n" +
                        "    \"login\": \"%s\",\n" +
                        "    \"name\": \"%s\",\n" +
                        "    \"birthday\": \"%s\"\n" +
                        "}",
                email, login, name, birthday
        );

        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(result);
        long id = root.get("id").asLong();

        return User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }

    @Test
    void shouldReturn400WhenLoginIsNull() throws Exception {
        String json = "{ \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.login").value("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void shouldReturn400WhenLoginIsEmpty() throws Exception {
        String json = "{ \"login\": \"\", \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.login").value("Логин не может быть пустым и содержать пробелы"));

    }

    @Test
    void shouldReturn400WhenLoginContainsSpaces() throws Exception {
        String json = "{ \"login\": \"user login\", \"email\": \"user@yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.login").value("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void shouldReturn400WhenEmailIsNull() throws Exception {
        String json = "{ \"login\": \"user\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.email").value("Email не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenEmailIsEmpty() throws Exception {
        String json = "{ \"login\": \"user\", \"email\": \"\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.email").value("Email не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenEmailWithoutAt() throws Exception {
        String json = "{ \"login\": \"user\", \"email\": \"user-yandex.ru\", \"birthday\": \"1990-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.email").value("Неверный формат email"));
    }

    @Test
    void shouldReturn400WhenBirthdayInFuture() throws Exception {
        String json = "{ \"login\": \"user\", \"email\": \"user@yandex.ru\", \"birthday\": \"3000-01-01\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
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

    @Test
    void shouldAddFriend() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", user1.getId(), user2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user2.getId()));
    }

    @Test
    void shouldRemoveFriend() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", user1.getId(), user2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", user1.getId(), user2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldGetFriendsList() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", user1.getId(), user2.getId())).andExpect(status().isOk());
        mockMvc.perform(put("/users/{id}/friends/{friendId}", user1.getId(), user3.getId())).andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.id == %d)]", user2.getId()).exists())
                .andExpect(jsonPath("$[?(@.id == %d)]", user3.getId()).exists());
    }

    @Test
    void shouldGetCommonFriends() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", user1.getId(), user3.getId())).andExpect(status().isOk());
        mockMvc.perform(put("/users/{id}/friends/{friendId}", user2.getId(), user3.getId())).andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", user1.getId(), user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(user3.getId()));
    }

    @Test
    void shouldReturnEmptyListWhenNoCommonFriends() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", user1.getId(), user2.getId())).andExpect(status().isOk());

        mockMvc.perform(put("/users/{id}/friends/{friendId}", user2.getId(), user1.getId())).andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", user1.getId(), user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        mockMvc.perform(put("/users/999/friends/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenFriendNotFound() throws Exception {
        mockMvc.perform(put("/users/1/friends/999"))
                .andExpect(status().isNotFound());
    }
}
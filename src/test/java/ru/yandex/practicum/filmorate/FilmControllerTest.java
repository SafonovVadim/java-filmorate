package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final LocalDate validDate = LocalDate.of(1895, 12, 28);

    private long createFilm(String name) throws Exception {
        Film film = Film.builder()
                .name(name)
                .description("desc")
                .releaseDate(validDate.plusDays(1))
                .duration(100)
                .build();

        String result = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractIdFromJson(result);
    }

    private long createUser(String email) throws Exception {
        User user = User.builder()
                .email(email)
                .login("user" + email.split("@")[0])
                .name("User")
                .birthday(validDate.plusYears(20))
                .build();

        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toUserJson(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractIdFromJson(result);
    }

    private String toJson(Object obj) {
        return """
                {
                  "name": "%s",
                  "description": "%s",
                  "releaseDate": "%s",
                  "duration": %d
                }
                """.formatted(
                ((Film) obj).getName(),
                ((Film) obj).getDescription(),
                ((Film) obj).getReleaseDate(),
                ((Film) obj).getDuration()
        );
    }

    private String toUserJson(Object obj) {
        return """
                {
                  "email": "%s",
                  "login": "%s",
                  "name": "%s",
                  "birthday": "%s"
                }
                """.formatted(
                ((User) obj).getEmail(),
                ((User) obj).getLogin(),
                ((User) obj).getName(),
                ((User) obj).getBirthday()
        );
    }

    @SneakyThrows
    private long extractIdFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        return root.get("id").asLong();
    }

    @Test
    @Order(1)
    void shouldReturnEmptyListWhenNoFilms() throws Exception {
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Order(2)
    void shouldGetPopularFilms() throws Exception {
        long film1 = createFilm("Popular 1");
        long film2 = createFilm("Popular 2");

        long user1 = createUser("u1@yandex.ru");
        long user2 = createUser("u2@yandex.ru");

        mockMvc.perform(put("/films/{id}/like/{userId}", film1, user1)).andExpect(status().isOk());
        mockMvc.perform(put("/films/{id}/like/{userId}", film1, user2)).andExpect(status().isOk());
        mockMvc.perform(put("/films/{id}/like/{userId}", film2, user1)).andExpect(status().isOk());

        mockMvc.perform(get("/films/popular").param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(film1))
                .andExpect(jsonPath("$[1].id").value(film2));
    }

    @Test
    void shouldReturn400WhenNameIsNull() throws Exception {
        String json = "{ \"description\": \"cool film\", \"releaseDate\": \"1999-01-01\", \"duration\": 120 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value("Название не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenNameIsEmpty() throws Exception {
        String json = "{ \"name\": \"\", \"description\": \"cool film\", \"releaseDate\": \"1999-01-01\", \"duration\": 120 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.name").value("Название не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenDescriptionTooLong() throws Exception {
        String longDesc = "a".repeat(201);
        String json = String.format("""
                 {
                                  "name": "Movie",
                                  "description": "%s",
                                  "releaseDate": "2000-01-01",
                                  "duration": 100
                                }\
                """, longDesc);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.description").value("Максимальная длина описания — 200 символов"));
    }

    @Test
    void shouldReturn400WhenReleaseDateTooEarly() throws Exception {
        String json = "{ \"name\": \"Old\", \"description\": \"old\", \"releaseDate\": \"1895-12-27\", \"duration\": 10 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.releaseDate").value("Дата релиза — не раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldReturn400WhenDurationIsZero() throws Exception {
        String json = "{ \"name\": \"Zero\", \"description\": \"zero\", \"releaseDate\": \"2000-01-01\", \"duration\": 0 }";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
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
        mockMvc.perform(delete("/films/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteFilm() throws Exception {
        long filmId = createFilm("To Delete");

        mockMvc.perform(delete("/films/{id}", filmId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films"))
                .andExpect(jsonPath("$[?(@.id == %d)]".formatted(filmId)).doesNotExist());
    }

    @Test
    void shouldReturn404WhenDeleteNonExistingFilm() throws Exception {
        mockMvc.perform(delete("/films/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldLikeFilm() throws Exception {
        long filmId = createFilm("Liked Film");
        long userId = createUser("like@yandex.ru");

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films"))
                .andExpect(jsonPath("$[?(@.id == %d)].likes".formatted(filmId)).isArray())
                .andExpect(jsonPath("$[?(@.id == %d)].likes.length()".formatted(filmId)).value(1));
        mockMvc.perform(delete("/films/{id}", filmId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenLikeNonExistingFilm() throws Exception {
        long userId = createUser("user@yandex.ru");
        mockMvc.perform(put("/films/{id}/like/{userId}", 999L, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenLikeFilmWithNonExistingUser() throws Exception {
        long filmId = createFilm("Film");
        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveLike() throws Exception {
        long filmId = createFilm("Remove Like");
        long userId = createUser("rem@yandex.ru");

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films"))
                .andExpect(jsonPath("$[?(@.id == %d)].likes.length()".formatted(filmId)).value(0));
    }

    @Test
    void shouldReturn404WhenRemoveLikeFromNonExistingFilm() throws Exception {
        long userId = createUser("user@yandex.ru");
        mockMvc.perform(delete("/films/{id}/like/{userId}", 999L, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenRemoveLikeByNonExistingUser() throws Exception {
        long filmId = createFilm("Film");
        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, 999L))
                .andExpect(status().isNotFound());
    }
}
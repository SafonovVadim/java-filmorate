package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    Long id;
    String name;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = "^[a-zA-Z0-9А-Яа-яЁё]+$", message = "Логин не может быть пустым и содержать пробелы")
    String login;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
    @Builder.Default
    private Set<Long> friends = new HashSet<>();

    public Set<Long> getFriends() {
        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends;
    }
}

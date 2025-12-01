package ru.yandex.practicum.filmorate.utils;

import java.util.Map;

public class UtilMethods {

    public static long getNextId(Map<Long, ?> map) {
        long currentMaxId = map.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

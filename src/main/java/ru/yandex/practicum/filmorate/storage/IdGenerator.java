package ru.yandex.practicum.filmorate.storage;

import java.util.Map;

public class IdGenerator {

    public static Long getNextId(Map<Long, ?> collection) {
        long currentMaxId = collection.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.Collection;
import java.util.List;

public interface EventStorage {
    void addEvent(Long userId, EventType eventType, Operation operation, Long entityId);

    List<Event> getEventsByUserIds(Collection<Long> userIds);
}

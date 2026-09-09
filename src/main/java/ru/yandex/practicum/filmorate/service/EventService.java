package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.mappers.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class EventService {

    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public EventService(EventStorage eventStorage, UserStorage userStorage) {
        this.eventStorage = eventStorage;
        this.userStorage = userStorage;
    }

    public Collection<EventDto> getFeed(Long userId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        List<Long> friendIds = userStorage.findFriends(userId).stream()
                .map(User::getId)
                .toList();

        if (friendIds.isEmpty()) {
            return List.of();
        }

        List<Event> events = eventStorage.getEventsByUserIds(friendIds);

        return events.stream()
                .map(EventMapper::mapToEventDto)
                .toList();
    }


}

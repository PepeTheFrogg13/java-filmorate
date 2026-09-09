package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;


import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;
    private final EventStorage eventStorage;
    private final EventService eventService;

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));

    }

    @Test
    public void testAllUsers() {

        Collection<User> users = userStorage.findAll();
        assertThat(users.size()).isEqualTo(3);

    }

    @Test
    public void testAddAndGetEvent() {
        eventStorage.addEvent(2L, EventType.LIKE, Operation.ADD, 1L);

        List<Event> events = eventStorage.getEventsByUserIds(List.of(2L));

        assertThat(events).anySatisfy(event -> {
                    assertThat(event.getUserId()).isEqualTo(2L);
                    assertThat(event.getEventType()).isEqualTo(EventType.LIKE);
                    assertThat(event.getOperation()).isEqualTo(Operation.ADD);
                    assertThat(event.getEntityId()).isEqualTo(1L);
                });
    }

    @Test
    public void testFeedContainsFriendEvent() {
        eventStorage.addEvent(2L, EventType.LIKE, Operation.ADD, 1L);

        Collection<EventDto> feed = eventService.getFeed(3L);

        assertThat(feed).anySatisfy(event -> {
                    assertThat(event.getUserId()).isEqualTo(2L);
                    assertThat(event.getEventType()).isEqualTo(EventType.LIKE);
                    assertThat(event.getOperation()).isEqualTo(Operation.ADD);
                    assertThat(event.getEntityId()).isEqualTo(1L);
                });
    }
}

package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class EventDbStorage extends BaseRepository<Event> implements EventStorage {

    private static final String GET_EVENTS = "SELECT * FROM events WHERE user_id IN (%s) ORDER BY timestamp DESC, " +
            "event_id DESC";

    private static final String ADD_EVENT = "INSERT INTO events (timestamp, user_id, event_type, operation, " +
            "entity_id) VALUES(?, ?, ?, ?, ?)";

    public EventDbStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        long timestamp = System.currentTimeMillis();
        jdbc.update(ADD_EVENT, timestamp, userId, eventType.name(), operation.name(), entityId);
    }

    @Override
    public List<Event> getEventsByUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(",", Collections.nCopies(userIds.size(), "?"));

        return findMany(GET_EVENTS.formatted(placeholders), userIds.toArray());
    }

}

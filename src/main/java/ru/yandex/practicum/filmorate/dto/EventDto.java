package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

@Data
public class EventDto {
    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private Long eventId;
    private Long timestamp;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;
    private EventType eventType;
    private Operation operation;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long entityId;
}

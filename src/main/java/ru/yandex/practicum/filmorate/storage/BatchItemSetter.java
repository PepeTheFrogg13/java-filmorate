package ru.yandex.practicum.filmorate.storage;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface BatchItemSetter<E> {
    void setValues(PreparedStatement ps, E item) throws SQLException;
}

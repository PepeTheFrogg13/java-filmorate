package ru.yandex.practicum.filmorate.service;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingStorage ratingStorage;

    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Collection<Rating> findAll() {
        return ratingStorage.findAll();
    }

    public Rating findById(Long id) {
        Optional<Rating> ratingOptional = ratingStorage.findById(id);
        if (ratingOptional.isEmpty()) {
            throw new IdNotFoundException("Рейтинг с id = " + id + " не найден");
        }
        return ratingOptional.get();
    }
}

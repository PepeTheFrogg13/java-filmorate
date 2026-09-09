package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    public DirectorService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    public List<Director> getAllDirectors() {
        return directorDbStorage.findAll();
    }

    public Director getDirectorById(Long id) {
        return directorDbStorage.findById(id);
    }

    public Director createDirector(Director director) {
        validateDirector(director);
        return directorDbStorage.save(director);
    }

    public Director updateDirector(Director director) {
        validateDirector(director);
        directorDbStorage.findById(director.getId());
        return directorDbStorage.update(director);
    }

    public void deleteDirector(Long id) {
        directorDbStorage.delete(id);
    }

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
    }
}
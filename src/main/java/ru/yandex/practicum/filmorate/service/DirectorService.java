package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import java.util.List;
import java.util.Optional;

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
        Optional<Director> directorOptional = directorDbStorage.findById(id);
        if (directorOptional.isEmpty()) {
            throw new IdNotFoundException("Режиссер с id = " + id + " не найден");
        }
        return directorOptional.get();
    }

    public Director createDirector(Director director) {
        validateDirector(director);
        return directorDbStorage.save(director);
    }

    public Director updateDirector(Director director) {
        validateDirector(director);
        Optional<Director> directorOptional = directorDbStorage.findById(director.getId());
        if (directorOptional.isEmpty()) {
            throw new IdNotFoundException("Режиссер с id = " + director.getId() + " не найден");
        }
        return directorDbStorage.update(director).get();
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

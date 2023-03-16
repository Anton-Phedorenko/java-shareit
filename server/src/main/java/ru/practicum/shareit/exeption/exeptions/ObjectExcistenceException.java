package ru.practicum.shareit.exeption.exeptions;

public class ObjectExcistenceException extends RuntimeException {
    public ObjectExcistenceException(final String message) {
        super(message);
    }
}

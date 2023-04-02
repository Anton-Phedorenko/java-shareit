package ru.practicum.shareit.exeption.exeptions;

public class ConflictException extends RuntimeException {
    public ConflictException(final String message) {
        super(message);
    }
}

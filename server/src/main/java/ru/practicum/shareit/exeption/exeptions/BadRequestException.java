package ru.practicum.shareit.exeption.exeptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(final String message) {
        super(message);
    }
}
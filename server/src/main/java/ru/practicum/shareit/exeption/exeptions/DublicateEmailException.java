package ru.practicum.shareit.exeption.exeptions;

public class DublicateEmailException extends RuntimeException {
    public DublicateEmailException(final String message) {
        super(message);
    }
}

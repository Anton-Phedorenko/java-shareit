package ru.practicum.shareit.exception;

public class WrongStateException extends RuntimeException {
    public WrongStateException(String error) {
        super(error);
    }
}

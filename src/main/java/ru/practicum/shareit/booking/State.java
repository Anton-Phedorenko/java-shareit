package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.WrongStateException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static class States {
        public static State getState(String name) {
            for (State state : State.values()) {
                if (state.name().equals(name)) {
                    return State.valueOf(name);
                }
            }
            throw new WrongStateException("Unknown state: " + name);
        }
    }
}
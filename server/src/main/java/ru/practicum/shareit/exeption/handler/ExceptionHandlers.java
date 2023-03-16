package ru.practicum.shareit.exeption.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exeption.exeptions.DublicateEmailException;
import ru.practicum.shareit.exeption.exeptions.ObjectExcistenceException;
import ru.practicum.shareit.exeption.exeptions.ValidationException;
import ru.practicum.shareit.exeption.response.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlers {

    @ExceptionHandler
    public ResponseEntity<String> exc(ValidationException ex) {
        log.info("Код ошибки: 400");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> exc(ObjectExcistenceException ex) {
        log.info("Код ошибки: 404");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exc(Throwable ex) {
        log.info("Код ошибки: 500");
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> exc(DublicateEmailException ex) {
        log.info("Код ошибки: 409");
        return new ResponseEntity<>("Поймано необработанное исключение", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> exc(MethodArgumentNotValidException ex) {
        log.info("Код ошибки 400");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerValidationException(final RuntimeException e) {
        log.warn("Конфликтующий запрос 409 {}", e.getMessage(), e);
        return new ErrorResponse(
                "Конфликтующий запрос",
                e.getMessage(), HttpStatus.CONFLICT.name(), LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final ObjectNotFoundException e) {
        log.warn("Объект не найден 404 {}", e.getMessage(), e);
        return new ErrorResponse(
                "Объект не найден",
                e.getMessage(), HttpStatus.NOT_FOUND.name(), LocalDateTime.now()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class,
            ConstraintViolationException.class, ValidationException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final RuntimeException e) {
        log.warn("Некорректные данные от пользователя 400 {}", e.getMessage(), e);
        return new ErrorResponse(
                "Некорректные данные",
                e.getMessage(), HttpStatus.BAD_REQUEST.name(), LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn("Произошла непредвиденная ошибка 500 {}", e.getMessage(), e);
        return new ErrorResponse(
                "Непредвиденная ошибка",
                e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.name(), LocalDateTime.now()
        );
    }
}

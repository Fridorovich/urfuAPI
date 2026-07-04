package org.urfu.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Перехватывает все исключения и возвращает структурированный JSON-ответ.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка исключения "Сущность не найдена".
     * Возвращает HTTP 404.
     *
     * @param ex исключение EntityNotFoundException
     * @return ответ с сообщением об ошибке
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        log.error("Entity not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Обработка исключения "Некорректный аргумент".
     * Возвращает HTTP 400.
     *
     * @param ex исключение IllegalArgumentException
     * @return ответ с сообщением об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка ошибок валидации DTO.
     * Возвращает HTTP 400 с деталями по каждому полю.
     *
     * @param ex исключение MethodArgumentNotValidException
     * @return ответ с деталями ошибок валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation failed");
        response.put("timestamp", LocalDateTime.now());
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Обработка всех остальных непредвиденных исключений.
     * Возвращает HTTP 500.
     *
     * @param ex исключение
     * @return ответ с общим сообщением об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Вспомогательный метод для построения стандартного JSON-ответа с ошибкой.
     *
     * @param message сообщение об ошибке
     * @param status  HTTP статус
     * @return сформированный ответ
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}
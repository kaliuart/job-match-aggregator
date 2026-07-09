package com.artur.jobaggregator.project;

import com.artur.jobaggregator.project.dto.ErrorResponseDto;
import com.artur.jobaggregator.project.exception.badrequest.BadRequestException;
import com.artur.jobaggregator.project.exception.conflict.ConflictException;
import com.artur.jobaggregator.project.exception.externalservice.ExternalServiceException;
import com.artur.jobaggregator.project.exception.notfound.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleConflictException(ConflictException e, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                "conflict",
                e.getMessage(),
                LocalDateTime.now(),
                409,
                request.getRequestURI()
        );
        logger.error("Handle" +  e.getClass().getName());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleNotFound(NotFoundException e, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                "not found",
                e.getMessage(),
                LocalDateTime.now(),
                404,
                request.getRequestURI()
        );
        logger.error("Handle" + e.getClass().getName());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleExternalService(ExternalServiceException e, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                "external service error",
                e.getMessage(),
                LocalDateTime.now(),
                500,
                request.getRequestURI()
        );
        logger.error("Handle" + e.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(exception = {
            BadRequestException.class,
            MethodValidationException.class
    })

    public ResponseEntity<ErrorResponseDto> handleBadRequest(Exception e, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                "bad request",
                e.getMessage(),
                LocalDateTime.now(),
                400,
                request.getRequestURI()
        );
        logger.error("Handle" + e.getClass().getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}

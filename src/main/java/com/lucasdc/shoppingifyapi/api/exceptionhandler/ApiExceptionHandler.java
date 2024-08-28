package com.lucasdc.shoppingifyapi.api.exceptionhandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.lucasdc.shoppingifyapi.domain.exception.EntityNotFoundException;
import com.lucasdc.shoppingifyapi.domain.exception.NegocioException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<?> handleNegocioException(NegocioException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Problem problem = Problem.builder()
            .userMessage(e.getMessage())
            .status(status.value())
            .timestamp(LocalDateTime.now())
            .build();
                
        
        return ResponseEntity.status(400).body(problem);
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleCategoryNotFoundException(EntityNotFoundException e) {
        
        HttpStatus status = HttpStatus.NOT_FOUND;
        String userMessage = e.getMessage();        
        
        Problem problem = Problem.builder()
        .timestamp(LocalDateTime.now())
        .userMessage(userMessage)
        .status(status.value())
        .build();
                
        
        return ResponseEntity.status(404).body(problem);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String userMessage = "O item esta sendo utilizado em uma lista de compras.";        
        
        Problem problem = Problem.builder()
        .timestamp(LocalDateTime.now())
        .userMessage(userMessage)
        .status(status.value())
        .build();
                
        
        return ResponseEntity.status(400).body(problem);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            org.springframework.http.HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult bindResult = ex.getBindingResult();

        List<Problem.Field> fields = bindResult.getFieldErrors().stream()
            .map(fieldError -> Problem.Field.builder()
                .name(fieldError.getField())
                .userMessage(fieldError.getDefaultMessage())
                .build())
            .collect(Collectors.toList());

        String message = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";
        
        Problem problem = Problem.builder()   
            .status(status.value())
            .fields(fields)
            .timestamp(LocalDateTime.now())     
            .userMessage(message)
            .build();

        
        return handleExceptionInternal(ex, problem, headers, status, request);
    }
    



}

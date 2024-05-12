package com.mitocode.exception;

import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleAllException(Exception ex, WebRequest request){
        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleModelNotFoundException(ModelNotFoundException ex, WebRequest request){
        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField().concat(":").concat(e.getDefaultMessage()))
                .collect(Collectors.joining(","));

        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), msg, request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    /*@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request){
        CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }*/

    /*@ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<CustomErrorRecord> handleModelNotFoundException(ModelNotFoundException ex, WebRequest request){
        CustomErrorRecord err = new CustomErrorRecord(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }*/

    //Desde Spring Boot 3
    /*@ExceptionHandler(ModelNotFoundException.class)
    public ProblemDetail handleModelNotFoundException(ModelNotFoundException ex, WebRequest request){
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Model Not Found Exception");
        pd.setType(URI.create(request.getDescription(false)));
        pd.setProperty("extra1", "extra-value");
        pd.setProperty("extra2", 33);
        return pd;
    }*/

    /*@ExceptionHandler(ModelNotFoundException.class)
    public ErrorResponse handleModelNotFoundException(ModelNotFoundException ex, WebRequest request){

        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
                .title("Model Not Found Exception")
                .type(URI.create(request.getDescription(false)))
                .property("extra1", "extra-value")
                .property("extra2", 33)
                .build();
    }*/

}

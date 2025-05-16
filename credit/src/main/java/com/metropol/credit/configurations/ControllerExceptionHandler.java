package com.metropol.credit.configurations;

import java.nio.file.AccessDeniedException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.metropol.credit.models.Message;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    Logger logger = LogManager.getLogger(ControllerExceptionHandler.class);

    @Autowired
    Pattern pattern;

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleError(AccessDeniedException exception, WebRequest webRequest) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Message("You don't have the permission required to perform this action"));

    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Message("Required request body is missing"));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "One or more of the fields are mal-formed or missing.");
        List<Object> errors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            Map<String, String> error = new HashMap<>();
            error.put(fieldError.getField(), fieldError.getDefaultMessage());
            errors.add(error);
        }
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleError(DataIntegrityViolationException exception, WebRequest webRequest) {
        exception.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new Message("Constraint violation OR entity with the same information already exists"));

    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<?> handleError(JWTVerificationException exception, WebRequest webRequest) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Token is invalid or already expired"));

    }

    @ExceptionHandler(JWTCreationException.class)
    public ResponseEntity<?> handleError(JWTCreationException exception, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(exception.getMessage()));

    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleError(CustomException exception, WebRequest webRequest) {
        return ResponseEntity.status(exception.statusCode).body(new Message(exception.getMessage()));

    }

    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<?> handleError(InvalidKeyException exception, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Invalid key"));

    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleError(SignatureException exception, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Invalid signature"));

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleError(IllegalArgumentException exception, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Oops, failed processing this request"));

    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleError(NoSuchElementException exception, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Requested item does not exist"));

    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "One or more of the fields are mal-formed or missing.");
        List<Object> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            Map<String, String> error = new HashMap<>();
            error.put(fieldError.getField(), fieldError.getDefaultMessage());
            errors.add(error);
        }
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleError(MissingRequestHeaderException exception, WebRequest webRequest) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "The following header name is required: " + exception.getHeaderName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "The following query parameter of type " + ex.getParameterType()
                + " is required: " + ex.getParameterName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Default if no match is found
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleError(Exception exception, WebRequest webRequest) {

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) exception;

            Map<String, Object> response = new HashMap<>();
            response.put("message", "One or more of the fields are mal-formed or missing.");
            List<Object> errors = new ArrayList<>();
            for (ConstraintViolation<?> eViolation : ex.getConstraintViolations()) {
                Map<String, String> error = new HashMap<>();
                Iterator<Path.Node> iterator = eViolation.getPropertyPath().iterator();
                while (iterator.hasNext()) {
                    Path.Node node = iterator.next();
                    if (node.getKind() == ElementKind.PROPERTY) {
                        error.put(node.getName(),
                                eViolation.getMessage());
                        errors.add(error);
                    }
                }

            }
            response.put("errors", errors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message(exception.getMessage()));

    }

}

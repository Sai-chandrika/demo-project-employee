package com.example.organizationEmployeeProjectManagement.globalException;

import com.example.organizationEmployeeProjectManagement.exception.*;
import com.example.organizationEmployeeProjectManagement.exception.NullPointerException;
import com.example.organizationEmployeeProjectManagement.security.dto.genericResponce.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 05-04-2023
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<GenericResponse> handleExceptions(
            Exception ex,
            WebRequest request
    ) {
        if (ex instanceof RuntimeException) {
            return composeMethodNotSupportedException(ex, request);
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            return composeMethodNotSupportedException(ex, request);
        } else if (ex instanceof AuthenticationBasedException) {
            return composeMethodNotSupportedException(ex, request);
        }else if (ex instanceof BadAlertException){
            return composeGenericException(ex,request);
        }else if(ex instanceof DuplicateValueException){
            return composeMethodNotSupportedException(ex, request);
        }else if (ex instanceof PatternNotMatchException){
            return composeGenericException(ex,request);
        }else {
            return composeGenericException(ex, request);
        }
    }


    private ResponseEntity<GenericResponse> composeMethodNotSupportedException(Exception ex, WebRequest request) {
        return new ResponseEntity<GenericResponse>(composeApiResponse(ex.getMessage(), 405, request.getDescription(true)), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<GenericResponse> composeRunTimeException(Exception ex, WebRequest request) {
        ResponseEntity<GenericResponse> response = null;
        if (ex instanceof NullPointerException || ex instanceof ClassCastException || ex instanceof IllegalArgumentException || ex instanceof NoSuchElementException || ex instanceof IndexOutOfBoundsException) {
            response = new ResponseEntity<GenericResponse>(composeApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getDescription(true)), HttpStatus.BAD_REQUEST);
        } else if (ex instanceof NotFoundException) {
            response = new ResponseEntity<GenericResponse>(composeApiResponse(ex.getMessage(), 404, request.getDescription(true)), HttpStatus.NOT_FOUND);
        } else if (ex instanceof AccessDeniedException) {
            response = new ResponseEntity<GenericResponse>(composeApiResponse(ex.getMessage(), 403, request.getDescription(true)), HttpStatus.FORBIDDEN);
        } else if (ex instanceof PatternNotMatchException) {
            response = new ResponseEntity<GenericResponse>(composeApiResponse(ex.getMessage(),400, request.getDescription(true)),HttpStatus.BAD_REQUEST);
        } else {
            response = new ResponseEntity<GenericResponse>(composeApiResponse(ex.getMessage(), 500, request.getDescription(true)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    private ResponseEntity<GenericResponse> composeGenericException(Exception ex, WebRequest request) {
        return new ResponseEntity<GenericResponse>(composeApiResponse(ex.getMessage(), 401, request.getDescription(true)), HttpStatus.UNAUTHORIZED);
    }


    private GenericResponse composeApiResponse(String message, int code, String payLoad) {
        return new GenericResponse(code, payLoad,message);
    }
}
package com.example.organizationEmployeeProjectManagement.exception;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 05-04-2023
 */
public class AuthenticationBasedException extends RuntimeException{
    public AuthenticationBasedException(String message) {
        super(message);
    }
}
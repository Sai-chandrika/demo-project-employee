package com.example.organizationEmployeeProjectManagement.exception;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 14-04-2023
 */
public class BadAlertException extends RuntimeException{
    public BadAlertException(String message) {
        super(message);
    }
}
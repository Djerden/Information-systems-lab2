package com.djeno.backend_lab1.exceptions;

public class StudyGroupNotFoundException extends RuntimeException {
    public StudyGroupNotFoundException(String message) {
        super(message);
    }
}
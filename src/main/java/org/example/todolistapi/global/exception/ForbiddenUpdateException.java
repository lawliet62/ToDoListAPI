package org.example.todolistapi.global.exception;

public class ForbiddenUpdateException extends RuntimeException {
    public ForbiddenUpdateException(String message) {
        super(message);
    }
}

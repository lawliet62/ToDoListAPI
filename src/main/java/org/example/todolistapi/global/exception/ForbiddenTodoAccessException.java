package org.example.todolistapi.global.exception;

public class ForbiddenTodoAccessException extends RuntimeException {
    public ForbiddenTodoAccessException(String message) {
        super(message);
    }
}

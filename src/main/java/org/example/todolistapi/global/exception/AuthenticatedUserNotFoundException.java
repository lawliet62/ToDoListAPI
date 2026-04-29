package org.example.todolistapi.global.exception;

public class AuthenticatedUserNotFoundException extends IllegalStateException {
    public AuthenticatedUserNotFoundException(String message) {
        super(message);
    }
}

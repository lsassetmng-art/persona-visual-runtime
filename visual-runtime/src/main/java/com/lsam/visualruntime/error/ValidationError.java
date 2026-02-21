package com.lsam.visualruntime.error;

public class ValidationError extends PersonaVisualException {
    public ValidationError(String message) { super(message); }
    public ValidationError(String message, Throwable cause) { super(message, cause); }
}

package com.lsam.visualruntime.error;

public class SecurityError extends PersonaVisualException {
    public SecurityError(String message) { super(message); }
    public SecurityError(String message, Throwable cause) { super(message, cause); }
}

package com.lsam.visualruntime.error;

public class NetworkError extends PersonaVisualException {
    public NetworkError(String message, Throwable cause) { super(message, cause); }
    public NetworkError(String message) { super(message); }
}

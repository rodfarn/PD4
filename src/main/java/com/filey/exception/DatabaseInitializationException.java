package com.filey.exception;

public class DatabaseInitializationException extends FileyException {
    public DatabaseInitializationException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

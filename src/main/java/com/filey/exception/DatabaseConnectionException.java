package com.filey.exception;

public class DatabaseConnectionException extends FileyException {
    public DatabaseConnectionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

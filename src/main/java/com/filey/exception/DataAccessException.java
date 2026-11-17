package com.filey.exception;

public class DataAccessException extends FileyException {
    public DataAccessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

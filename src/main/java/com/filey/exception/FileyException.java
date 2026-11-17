package com.filey.exception;

/** Excepción base para los errores controlados de la aplicación. */
public class FileyException extends RuntimeException {
    public FileyException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

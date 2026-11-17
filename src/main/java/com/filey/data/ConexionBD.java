package com.filey.data;

import com.filey.exception.DatabaseConnectionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD implements AutoCloseable {
    private static final String URL = "jdbc:h2:mem:filey;DB_CLOSE_DELAY=-1";
    private static final String USUARIO = "sa";
    private static final String CONTRASENA = "";

    private Connection conexion;

    public Connection conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            return conexion;
        } catch (SQLException error) {
            throw new DatabaseConnectionException("No se pudo conectar con la base de datos.", error);
        }
    }

    public String getUrl() {
        return URL;
    }

    @Override
    public void close() {
        if (conexion == null) {
            return;
        }
        try {
            conexion.close();
        } catch (SQLException error) {
            throw new DatabaseConnectionException("No se pudo cerrar la conexión a la base de datos.", error);
        }
    }
}

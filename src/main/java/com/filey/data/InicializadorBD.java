package com.filey.data;

import com.filey.exception.DatabaseInitializationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.math.BigDecimal;

public class InicializadorBD {
    public void crearTablasYDatos(Connection conexion) {
        try {
            crearTablas(conexion);
            insertarDatosDeEjemplo(conexion);
        } catch (SQLException error) {
            throw new DatabaseInitializationException(
                    "No se pudieron crear las tablas o cargar los datos de ejemplo.", error);
        }
    }

    private void crearTablas(Connection conexion) throws SQLException {
        String tablaEditorial = "CREATE TABLE IF NOT EXISTS EDITORIAL ("
                + "id INTEGER PRIMARY KEY, nombre VARCHAR(100) NOT NULL, "
                + "contacto VARCHAR(100) NOT NULL)";
        String tablaStand = "CREATE TABLE IF NOT EXISTS STAND ("
                + "identificador VARCHAR(20) PRIMARY KEY, ancho DECIMAL(8,2) NOT NULL, "
                + "largo DECIMAL(8,2) NOT NULL, precio DECIMAL(10,2) NOT NULL, "
                + "estado VARCHAR(20) NOT NULL)";
        String tablaReserva = "CREATE TABLE IF NOT EXISTS RESERVA ("
                + "id INTEGER PRIMARY KEY, stand_id VARCHAR(20) NOT NULL UNIQUE, "
                + "editorial_id INTEGER NOT NULL, fecha_reserva DATE NOT NULL, "
                + "fecha_vencimiento DATE NOT NULL, fecha_limite_pronto_pago DATE NOT NULL, "
                + "fecha_limite_liquidacion DATE NOT NULL, "
                + "FOREIGN KEY (stand_id) REFERENCES STAND(identificador), "
                + "FOREIGN KEY (editorial_id) REFERENCES EDITORIAL(id))";

        ejecutar(conexion, tablaEditorial);
        ejecutar(conexion, tablaStand);
        ejecutar(conexion, tablaReserva);
    }

    private void insertarDatosDeEjemplo(Connection conexion) throws SQLException {
        ejecutar(conexion, "DELETE FROM RESERVA");
        ejecutar(conexion, "DELETE FROM STAND");
        ejecutar(conexion, "DELETE FROM EDITORIAL");

        String insertarEditorial = "INSERT INTO EDITORIAL (id, nombre, contacto) VALUES (?, ?, ?)";
        try (PreparedStatement consulta = conexion.prepareStatement(insertarEditorial)) {
            consulta.setInt(1, 1);
            consulta.setString(2, "Editorial FILEY");
            consulta.setString(3, "contacto@filey.mx");
            consulta.executeUpdate();

            consulta.setInt(1, 2);
            consulta.setString(2, "Libros del Mayab");
            consulta.setString(3, "ventas@mayab.mx");
            consulta.executeUpdate();
        }

        String insertarStand = "INSERT INTO STAND "
                + "(identificador, ancho, largo, precio, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement consulta = conexion.prepareStatement(insertarStand)) {
            agregarStand(consulta, "A-01", "2", "2", "6000", "RESERVADO");
            agregarStand(consulta, "A-02", "3", "2", "9000", "DISPONIBLE");
            agregarStand(consulta, "B-01", "4", "2", "12000", "BLOQUEADO");
        }

        LocalDate fechaReserva = LocalDate.now();
        String insertarReserva = "INSERT INTO RESERVA "
                + "(id, stand_id, editorial_id, fecha_reserva, fecha_vencimiento, "
                + "fecha_limite_pronto_pago, fecha_limite_liquidacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement consulta = conexion.prepareStatement(insertarReserva)) {
            consulta.setInt(1, 1);
            consulta.setString(2, "A-01");
            consulta.setInt(3, 1);
            consulta.setDate(4, java.sql.Date.valueOf(fechaReserva));
            consulta.setDate(5, java.sql.Date.valueOf(fechaReserva.plusDays(15)));
            consulta.setDate(6, java.sql.Date.valueOf(fechaReserva.plusDays(7)));
            consulta.setDate(7, java.sql.Date.valueOf(fechaReserva.plusDays(60)));
            consulta.executeUpdate();
        }
    }

    private void ejecutar(Connection conexion, String sql) throws SQLException {
        try (Statement consulta = conexion.createStatement()) {
            consulta.executeUpdate(sql);
        }
    }

    private void agregarStand(
            PreparedStatement consulta,
            String identificador,
            String ancho,
            String largo,
            String precio,
            String estado) throws SQLException {
        consulta.setString(1, identificador);
        consulta.setBigDecimal(2, new BigDecimal(ancho));
        consulta.setBigDecimal(3, new BigDecimal(largo));
        consulta.setBigDecimal(4, new BigDecimal(precio));
        consulta.setString(5, estado);
        consulta.executeUpdate();
    }
}

package com.filey.data;

import com.filey.exception.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Centraliza las consultas DELETE de la aplicación. */
public class DeleteQuery {
    private final Connection conexion;

    public DeleteQuery(Connection conexion) {
        if (conexion == null) {
            throw new IllegalArgumentException("La conexión a la base de datos es obligatoria.");
        }
        this.conexion = conexion;
    }

    public boolean eliminarReserva(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador de la reserva debe ser positivo.");
        }
        return eliminar("DELETE FROM RESERVA WHERE id = ?", id,
                "No se pudo eliminar la reserva.");
    }

    public boolean eliminarStand(String identificador) {
        if (identificador == null || identificador.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del stand es obligatorio.");
        }
        return eliminar("DELETE FROM STAND WHERE identificador = ?", identificador.trim(),
                "No se pudo eliminar el stand. Verifique que no tenga una reserva asociada.");
    }

    public boolean eliminarEditorial(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador de la editorial debe ser positivo.");
        }
        return eliminar("DELETE FROM EDITORIAL WHERE id = ?", id,
                "No se pudo eliminar la editorial. Verifique que no tenga una reserva asociada.");
    }

    private boolean eliminar(String sql, Object identificador, String mensajeError) {
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setObject(1, identificador);
            return consulta.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new DataAccessException(mensajeError, error);
        }
    }
}

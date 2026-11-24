package com.filey.data;

import com.filey.domain.EstadoStand;
import com.filey.exception.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

/** Centraliza las consultas UPDATE de la aplicación. */
public class UpdateQuery {
    private final Connection conexion;

    public UpdateQuery(Connection conexion) {
        if (conexion == null) {
            throw new IllegalArgumentException("La conexión a la base de datos es obligatoria.");
        }
        this.conexion = conexion;
    }

    public boolean actualizarEstadoStand(String identificador, EstadoStand estado) {
        if (identificador == null || identificador.trim().isEmpty() || estado == null) {
            throw new IllegalArgumentException("El identificador y el estado del stand son obligatorios.");
        }

        String sql = "UPDATE STAND SET estado = ? WHERE identificador = ?";
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, estado.name());
            consulta.setString(2, identificador.trim());
            return consulta.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new DataAccessException("No se pudo actualizar el estado del stand.", error);
        }
    }

    public boolean actualizarEditorial(int id, String nombre, String contacto) {
        if (id <= 0 || nombre == null || nombre.trim().isEmpty()
                || contacto == null || contacto.trim().isEmpty()) {
            throw new IllegalArgumentException("Los datos de la editorial son obligatorios.");
        }

        String sql = "UPDATE EDITORIAL SET nombre = ?, contacto = ? WHERE id = ?";
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, nombre.trim());
            consulta.setString(2, contacto.trim());
            consulta.setInt(3, id);
            return consulta.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new DataAccessException("No se pudo actualizar la editorial.", error);
        }
    }

    public boolean actualizarFechasReserva(
            int id,
            LocalDate fechaReserva,
            LocalDate fechaVencimiento,
            LocalDate fechaLimiteProntoPago,
            LocalDate fechaLimiteLiquidacion) {
        if (id <= 0 || fechaReserva == null || fechaVencimiento == null
                || fechaLimiteProntoPago == null || fechaLimiteLiquidacion == null) {
            throw new IllegalArgumentException("El identificador y las fechas de la reserva son obligatorios.");
        }

        String sql = "UPDATE RESERVA SET fecha_reserva = ?, fecha_vencimiento = ?, "
                + "fecha_limite_pronto_pago = ?, fecha_limite_liquidacion = ? WHERE id = ?";
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setDate(1, java.sql.Date.valueOf(fechaReserva));
            consulta.setDate(2, java.sql.Date.valueOf(fechaVencimiento));
            consulta.setDate(3, java.sql.Date.valueOf(fechaLimiteProntoPago));
            consulta.setDate(4, java.sql.Date.valueOf(fechaLimiteLiquidacion));
            consulta.setInt(5, id);
            return consulta.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new DataAccessException("No se pudieron actualizar las fechas de la reserva.", error);
        }
    }
}

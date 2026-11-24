package com.filey.data;

import com.filey.domain.Editorial;
import com.filey.domain.Reserva;
import com.filey.domain.Stand;
import com.filey.exception.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Centraliza las consultas INSERT de la aplicación. */
public class InsertQuery {
    private final Connection conexion;

    public InsertQuery(Connection conexion) {
        if (conexion == null) {
            throw new IllegalArgumentException("La conexión a la base de datos es obligatoria.");
        }
        this.conexion = conexion;
    }

    public void insertarEditorial(int id, Editorial editorial) {
        if (id <= 0 || editorial == null) {
            throw new IllegalArgumentException("El identificador y la editorial son obligatorios.");
        }

        String sql = "INSERT INTO EDITORIAL (id, nombre, contacto) VALUES (?, ?, ?)";
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, id);
            consulta.setString(2, editorial.getNombre());
            consulta.setString(3, editorial.getContacto());
            consulta.executeUpdate();
        } catch (SQLException error) {
            throw new DataAccessException("No se pudo insertar la editorial.", error);
        }
    }

    public void insertarStand(Stand stand) {
        if (stand == null) {
            throw new IllegalArgumentException("El stand es obligatorio.");
        }

        String sql = "INSERT INTO STAND "
                + "(identificador, ancho, largo, precio, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, stand.getIdentificador());
            consulta.setBigDecimal(2, stand.getAncho());
            consulta.setBigDecimal(3, stand.getLargo());
            consulta.setBigDecimal(4, stand.getPrecio());
            consulta.setString(5, stand.getEstado().name());
            consulta.executeUpdate();
        } catch (SQLException error) {
            throw new DataAccessException("No se pudo insertar el stand.", error);
        }
    }

    public void insertarReserva(int id, Reserva reserva) {
        if (id <= 0 || reserva == null) {
            throw new IllegalArgumentException("El identificador y la reserva son obligatorios.");
        }

        String sql = "INSERT INTO RESERVA "
                + "(id, stand_id, editorial_id, fecha_reserva, fecha_vencimiento, "
                + "fecha_limite_pronto_pago, fecha_limite_liquidacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, id);
            consulta.setString(2, reserva.getStand().getIdentificador());
            consulta.setInt(3, reserva.getEditorial().getId());
            consulta.setDate(4, java.sql.Date.valueOf(reserva.getFechaReserva()));
            consulta.setDate(5, java.sql.Date.valueOf(reserva.getFechaVencimiento()));
            consulta.setDate(6, java.sql.Date.valueOf(reserva.getFechaLimiteProntoPago()));
            consulta.setDate(7, java.sql.Date.valueOf(reserva.getFechaLimiteLiquidacion()));
            consulta.executeUpdate();
        } catch (SQLException error) {
            throw new DataAccessException("No se pudo insertar la reserva.", error);
        }
    }
}

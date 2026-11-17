package com.filey.data;

import com.filey.domain.Editorial;
import com.filey.domain.EstadoStand;
import com.filey.domain.Reserva;
import com.filey.domain.Stand;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioFileyJDBC {
    private Connection conexion;

    public RepositorioFileyJDBC(Connection conexion) {
        this.conexion = conexion;
    }

    public List<Stand> listarStands() throws SQLException {
        List<Stand> stands = new ArrayList<Stand>();
        String sql = "SELECT identificador, ancho, largo, precio, estado FROM STAND";

        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                Stand stand = new Stand(
                        resultado.getString("identificador"),
                        resultado.getBigDecimal("ancho"),
                        resultado.getBigDecimal("largo"),
                        resultado.getBigDecimal("precio"),
                        EstadoStand.valueOf(resultado.getString("estado")));
                stands.add(stand);
            }
        }
        return stands;
    }

    public List<Editorial> listarEditoriales() throws SQLException {
        List<Editorial> editoriales = new ArrayList<Editorial>();
        String sql = "SELECT id, nombre, contacto FROM EDITORIAL";

        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                Editorial editorial = new Editorial(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString("contacto"));
                editoriales.add(editorial);
            }
        }
        return editoriales;
    }

    public List<Reserva> listarReservas() throws SQLException {
        List<Reserva> reservas = new ArrayList<Reserva>();
        String sql = "SELECT r.id AS reserva_id, s.identificador, s.ancho, s.largo, s.precio, s.estado, "
                + "e.id AS editorial_id, e.nombre, e.contacto, "
                + "r.fecha_reserva, r.fecha_vencimiento, "
                + "r.fecha_limite_pronto_pago, r.fecha_limite_liquidacion "
                + "FROM RESERVA r "
                + "JOIN STAND s ON r.stand_id = s.identificador "
                + "JOIN EDITORIAL e ON r.editorial_id = e.id";

        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                Stand stand = new Stand(
                        resultado.getString("identificador"),
                        resultado.getBigDecimal("ancho"),
                        resultado.getBigDecimal("largo"),
                        resultado.getBigDecimal("precio"),
                        EstadoStand.valueOf(resultado.getString("estado")));
                Editorial editorial = new Editorial(
                        resultado.getInt("editorial_id"),
                        resultado.getString("nombre"),
                        resultado.getString("contacto"));
                Reserva reserva = new Reserva(
                        resultado.getInt("reserva_id"),
                        stand,
                        editorial,
                        resultado.getDate("fecha_reserva").toLocalDate(),
                        resultado.getDate("fecha_vencimiento").toLocalDate(),
                        resultado.getDate("fecha_limite_pronto_pago").toLocalDate(),
                        resultado.getDate("fecha_limite_liquidacion").toLocalDate());
                reservas.add(reserva);
            }
        }
        return reservas;
    }
}

package com.filey.controller;

import com.filey.data.DeleteQuery;
import com.filey.data.InsertQuery;
import com.filey.data.ReadQuery;
import com.filey.data.UpdateQuery;
import com.filey.domain.Editorial;
import com.filey.domain.EstadoStand;
import com.filey.domain.Reserva;
import com.filey.domain.Stand;
import java.util.List;

/** Coordina las operaciones JDBC que se usan desde el menú. */
public class ControladorCRUD {
    private final InsertQuery inserciones;
    private final ReadQuery lecturas;
    private final UpdateQuery actualizaciones;
    private final DeleteQuery eliminaciones;

    public ControladorCRUD(InsertQuery inserciones, ReadQuery lecturas,
            UpdateQuery actualizaciones, DeleteQuery eliminaciones) {
        this.inserciones = inserciones;
        this.lecturas = lecturas;
        this.actualizaciones = actualizaciones;
        this.eliminaciones = eliminaciones;
    }

    public List<Stand> listarStands() {
        return lecturas.listarStands();
    }

    public List<Editorial> listarEditoriales() {
        return lecturas.listarEditoriales();
    }

    public List<Reserva> listarReservas() {
        return lecturas.listarReservas();
    }

    public void crearStand(Stand stand) {
        inserciones.insertarStand(stand);
    }

    public void crearEditorial(Editorial editorial) {
        inserciones.insertarEditorial(siguienteIdEditorial(), editorial);
    }

    public void crearReserva(Reserva reserva) {
        EstadoStand estado = reserva.getStand().getEstado();
        if (estado != EstadoStand.DISPONIBLE && estado != EstadoStand.BLOQUEADO) {
            throw new IllegalStateException("El stand no está disponible para reservar.");
        }

        inserciones.insertarReserva(siguienteIdReserva(), reserva);
        actualizaciones.actualizarEstadoStand(
                reserva.getStand().getIdentificador(), EstadoStand.RESERVADO);
    }

    public boolean actualizarStand(String id, EstadoStand estado) {
        Reserva reserva = buscarReservaPorStand(id);
        boolean tieneReserva = reserva != null;
        boolean debeTenerReserva = estado == EstadoStand.RESERVADO
                || estado == EstadoStand.OCUPADO;

        if (tieneReserva && !debeTenerReserva) {
            throw new IllegalStateException(
                    "Elimine primero la reserva asociada antes de liberar o bloquear el stand.");
        }
        if (!tieneReserva && debeTenerReserva) {
            throw new IllegalStateException(
                    "Un stand reservado u ocupado debe tener una reserva asociada.");
        }
        return actualizaciones.actualizarEstadoStand(id, estado);
    }

    public boolean actualizarEditorial(int id, String nombre, String contacto) {
        return actualizaciones.actualizarEditorial(id, nombre, contacto);
    }

    public boolean actualizarReserva(Reserva reserva) {
        return actualizaciones.actualizarFechasReserva(
                reserva.getId(), reserva.getFechaReserva(),
                reserva.getFechaVencimiento(), reserva.getFechaLimiteProntoPago(),
                reserva.getFechaLimiteLiquidacion());
    }

    public boolean eliminarStand(String id) {
        return eliminaciones.eliminarStand(id);
    }

    public boolean eliminarEditorial(int id) {
        return eliminaciones.eliminarEditorial(id);
    }

    public boolean eliminarReserva(int id) {
        Reserva reserva = buscarReserva(id);
        if (reserva == null) {
            return false;
        }

        boolean eliminada = eliminaciones.eliminarReserva(id);
        if (eliminada) {
            actualizaciones.actualizarEstadoStand(
                    reserva.getStand().getIdentificador(), EstadoStand.DISPONIBLE);
        }
        return eliminada;
    }

    private int siguienteIdEditorial() {
        int mayorId = 0;
        List<Editorial> editoriales = lecturas.listarEditoriales();
        for (Editorial editorial : editoriales) {
            if (editorial.getId() > mayorId) {
                mayorId = editorial.getId();
            }
        }
        return mayorId + 1;
    }

    private int siguienteIdReserva() {
        int mayorId = 0;
        List<Reserva> reservas = lecturas.listarReservas();
        for (Reserva reserva : reservas) {
            if (reserva.getId() > mayorId) {
                mayorId = reserva.getId();
            }
        }
        return mayorId + 1;
    }

    private Reserva buscarReserva(int id) {
        List<Reserva> reservas = lecturas.listarReservas();
        for (Reserva reserva : reservas) {
            if (reserva.getId() == id) {
                return reserva;
            }
        }
        return null;
    }

    private Reserva buscarReservaPorStand(String idStand) {
        List<Reserva> reservas = lecturas.listarReservas();
        for (Reserva reserva : reservas) {
            if (reserva.getStand().getIdentificador().equals(idStand)) {
                return reserva;
            }
        }
        return null;
    }
}

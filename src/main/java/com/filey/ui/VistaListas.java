package com.filey.ui;

import com.filey.domain.Editorial;
import com.filey.domain.Reserva;
import com.filey.domain.Stand;
import java.util.List;

public class VistaListas {
    public void mostrarConexion(String url) {
        System.out.println("Conexión a la base de datos establecida: " + url);
    }

    public void mostrarStands(List<Stand> stands) {
        System.out.println("\n--- Tabla STAND ---");
        for (Stand stand : stands) {
            System.out.println(stand.getIdentificador() + " | " + stand.getEstado()
                    + " | " + stand.metrosCuadrados() + " m2 | $" + stand.getPrecio());
        }
    }

    public void mostrarEditoriales(List<Editorial> editoriales) {
        System.out.println("\n--- Tabla EDITORIAL ---");
        for (Editorial editorial : editoriales) {
            System.out.println(editorial.getId() + " | " + editorial.getNombre()
                    + " | " + editorial.getContacto());
        }
    }

    public void mostrarReservas(List<Reserva> reservas) {
        System.out.println("\n--- Tabla RESERVA ---");
        for (Reserva reserva : reservas) {
            System.out.println(reserva.getId() + " | " + reserva.getStand().getIdentificador()
                    + " | Editorial: " + reserva.getEditorial().getNombre()
                    + " | Fecha: " + reserva.getFechaReserva()
                    + " | Vence: " + reserva.getFechaVencimiento());
        }
    }

    public void mostrarError(String mensaje) {
        System.out.println("Error al leer la base de datos: " + mensaje);
    }
}

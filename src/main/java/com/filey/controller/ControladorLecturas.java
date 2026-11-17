package com.filey.controller;

import com.filey.data.ReadQuery;
import com.filey.ui.VistaListas;

public class ControladorLecturas {
    private final ReadQuery readQuery;
    private final VistaListas vista;

    public ControladorLecturas(
            ReadQuery readQuery,
            VistaListas vista) {
        this.readQuery = readQuery;
        this.vista = vista;
    }

    public void mostrarListas() {
        vista.mostrarStands(readQuery.listarStands());
        vista.mostrarEditoriales(readQuery.listarEditoriales());
        vista.mostrarReservas(readQuery.listarReservas());
    }
}

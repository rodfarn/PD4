package com.filey.controller;

import com.filey.data.RepositorioFileyJDBC;
import com.filey.ui.VistaListas;
import java.sql.SQLException;

public class ControladorLecturas {
    private RepositorioFileyJDBC repositorio;
    private VistaListas vista;

    public ControladorLecturas(RepositorioFileyJDBC repositorio, VistaListas vista) {
        this.repositorio = repositorio;
        this.vista = vista;
    }

    public void mostrarListas() throws SQLException {
        vista.mostrarStands(repositorio.listarStands());
        vista.mostrarEditoriales(repositorio.listarEditoriales());
        vista.mostrarReservas(repositorio.listarReservas());
    }
}

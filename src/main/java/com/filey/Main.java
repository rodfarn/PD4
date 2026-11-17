package com.filey;

import com.filey.controller.ControladorLecturas;
import com.filey.data.ConexionBD;
import com.filey.data.InicializadorBD;
import com.filey.data.ReadQuery;
import com.filey.exception.FileyException;
import com.filey.ui.Consola;
import com.filey.ui.VistaListas;
import java.sql.Connection;

public class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ConexionBD proveedor = new ConexionBD();
        VistaListas vista = new VistaListas();

        try (ConexionBD sesion = proveedor) {
            Connection conexion = sesion.conectar();
            vista.mostrarConexion(sesion.getUrl());

            InicializadorBD inicializador = new InicializadorBD();
            inicializador.crearTablasYDatos(conexion);

            ReadQuery readQuery = new ReadQuery(conexion);
            ControladorLecturas controlador = new ControladorLecturas(readQuery, vista);
            controlador.mostrarListas();
        } catch (FileyException error) {
            vista.mostrarError(error.getMessage());
            return;
        }

        new Consola().iniciar();
    }
}

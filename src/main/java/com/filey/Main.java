package com.filey;

import com.filey.controller.ControladorLecturas;
import com.filey.data.ConexionBD;
import com.filey.data.InicializadorBD;
import com.filey.data.RepositorioFileyJDBC;
import com.filey.ui.Consola;
import com.filey.ui.VistaListas;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ConexionBD proveedor = new ConexionBD();
        VistaListas vista = new VistaListas();

        try (Connection conexion = proveedor.conectar()) {
            vista.mostrarConexion(conexion.getMetaData().getURL());

            InicializadorBD inicializador = new InicializadorBD();
            inicializador.crearTablasYDatos(conexion);

            RepositorioFileyJDBC repositorio = new RepositorioFileyJDBC(conexion);
            ControladorLecturas controlador = new ControladorLecturas(repositorio, vista);
            controlador.mostrarListas();
        } catch (SQLException error) {
            vista.mostrarError(error.getMessage());
            return;
        }

        new Consola().iniciar();
    }
}

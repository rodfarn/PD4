package com.filey;

import com.filey.controller.ControladorCRUD;
import com.filey.controller.ControladorLecturas;
import com.filey.data.ConexionBD;
import com.filey.data.DeleteQuery;
import com.filey.data.InsertQuery;
import com.filey.data.InicializadorBD;
import com.filey.data.ReadQuery;
import com.filey.data.UpdateQuery;
import com.filey.exception.FileyException;
import com.filey.ui.Consola;
import com.filey.ui.VistaListas;
import java.sql.Connection;

public class Main {
    private Main() {
    }

    public static void main(String[] args) {
        VistaListas vista = new VistaListas();

        try (ConexionBD sesion = new ConexionBD()) {
            Connection conexion = sesion.conectar();
            vista.mostrarConexion(sesion.getUrl());

            InicializadorBD inicializador = new InicializadorBD();
            inicializador.crearTablasYDatos(conexion);

            ReadQuery readQuery = new ReadQuery(conexion);
            ControladorLecturas controlador = new ControladorLecturas(readQuery, vista);
            controlador.mostrarListas();
            ControladorCRUD crud = new ControladorCRUD(
                    new InsertQuery(conexion), readQuery,
                    new UpdateQuery(conexion), new DeleteQuery(conexion));
            new Consola(crud).iniciar();
        } catch (FileyException error) {
            vista.mostrarError(error.getMessage());
        }
    }
}

package com.filey.ui;

import com.filey.domain.Editorial;
import com.filey.domain.EstadoStand;
import com.filey.domain.Reserva;
import com.filey.domain.Stand;
import com.filey.controller.ControladorCRUD;
import com.filey.notification.Notificacion;
import com.filey.service.SistemaFiley;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Consola {
    private final Scanner entrada = new Scanner(System.in);
    private final SistemaFiley sistema = new SistemaFiley();
    private final ControladorCRUD crud;

    public Consola(ControladorCRUD crud) {
        this.crud = crud;
    }

    public void iniciar() {
        System.out.println("=== Sistema Administrativo de Stands FILEY ===");
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            try {
                continuar = ejecutar(entrada.nextLine().trim());
            } catch (IllegalArgumentException | IllegalStateException error) {
                System.out.println("Error: " + error.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("1. Configurar costos y plazos");
        System.out.println("2. Registrar stand");
        System.out.println("3. Mostrar stands");
        System.out.println("4. Reservar stand");
        System.out.println("5. Bloquear stand");
        System.out.println("6. Desbloquear stand");
        System.out.println("7. Transferir bloqueado a reserva");
        System.out.println("8. Registrar pago");
        System.out.println("9. Revisar notificaciones");
        System.out.println("10. Administrar stands, editoriales y reservas");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private boolean ejecutar(String opcion) {
        switch (opcion) {
            case "1": configurar(); break;
            case "2": registrarStand(); break;
            case "3": mostrarStands(); break;
            case "4": reservar(false); break;
            case "5": sistema.bloquear(leerTexto("Identificador del stand: ")); break;
            case "6": sistema.desbloquear(leerTexto("Identificador del stand: ")); break;
            case "7": reservar(true); break;
            case "8": registrarPago(); break;
            case "9": mostrarNotificaciones(); break;
            case "10": menuCrud(); break;
            case "0": return false;
            default: System.out.println("Opción no válida.");
        }
        return true;
    }

    private void menuCrud() {
        while (true) {
            System.out.println("\n--- Administración de stands, editoriales y reservas ---");
            System.out.println("1. Crear registro");
            System.out.println("2. Consultar registros");
            System.out.println("3. Actualizar registro");
            System.out.println("4. Eliminar registro");
            System.out.println("0. Volver");
            String operacion = leerTexto("Operación: ");
            if ("0".equals(operacion)) {
                return;
            }
            try {
                String entidad = seleccionarEntidad();
                switch (operacion) {
                    case "1":
                        crear(entidad);
                        break;
                    case "2":
                        consultar(entidad);
                        break;
                    case "3":
                        actualizar(entidad);
                        break;
                    case "4":
                        eliminar(entidad);
                        break;
                    default:
                        System.out.println("Operación no válida.");
                }
            } catch (RuntimeException error) {
                System.out.println("Error: " + error.getMessage());
            }
        }
    }

    private String seleccionarEntidad() {
        System.out.println("1. Stand  2. Editorial  3. Reserva");
        String opcion = leerTexto("Entidad: ");
        if (!"1".equals(opcion) && !"2".equals(opcion) && !"3".equals(opcion)) {
            throw new IllegalArgumentException("Seleccione una entidad válida.");
        }
        return opcion;
    }

    private void crear(String entidad) {
        switch (entidad) {
            case "1":
                crud.crearStand(new Stand(leerTexto("Identificador: "),
                        leerDecimal("Ancho: "), leerDecimal("Largo: "),
                        leerDecimal("Precio total: "), EstadoStand.DISPONIBLE));
                break;
            case "2":
                crud.crearEditorial(new Editorial(leerTexto("Nombre: "), leerTexto("Contacto: ")));
                break;
            case "3":
                List<Stand> stands = crud.listarStands();
                List<Editorial> editoriales = crud.listarEditoriales();
                if (stands.isEmpty() || editoriales.isEmpty()) {
                    throw new IllegalStateException("Registre primero un stand y una editorial.");
                }
                String standId = leerTexto("Identificador del stand: ");
                int editorialId = leerEntero("ID de editorial: ");
                Stand stand = buscarStand(stands, standId);
                Editorial editorial = buscarEditorial(editoriales, editorialId);
                LocalDate fecha = leerFecha("Fecha de reserva (AAAA-MM-DD): ");
                LocalDate vence = leerFecha("Fecha de vencimiento (AAAA-MM-DD): ");
                LocalDate pronto = leerFecha("Fecha límite pronto pago (AAAA-MM-DD): ");
                LocalDate liquidacion = leerFecha("Fecha límite liquidación (AAAA-MM-DD): ");
                crud.crearReserva(new Reserva(stand, editorial, fecha, vence, pronto, liquidacion));
                break;
            default: throw new IllegalArgumentException("Entidad no válida.");
        }
        System.out.println("Registro creado en la base de datos.");
    }

    private void consultar(String entidad) {
        switch (entidad) {
            case "1":
                for (Stand stand : crud.listarStands()) {
                    System.out.printf("%s | %s | %s x %s | $%s%n", stand.getIdentificador(),
                            stand.getEstado(), stand.getAncho(), stand.getLargo(), stand.getPrecio());
                }
                break;
            case "2":
                for (Editorial editorial : crud.listarEditoriales()) {
                    System.out.printf("%d | %s | %s%n", editorial.getId(), editorial.getNombre(), editorial.getContacto());
                }
                break;
            case "3":
                for (Reserva reserva : crud.listarReservas()) {
                    System.out.printf("%d | Stand %s | Editorial %d: %s | %s a %s%n", reserva.getId(),
                            reserva.getStand().getIdentificador(), reserva.getEditorial().getId(),
                            reserva.getEditorial().getNombre(), reserva.getFechaReserva(), reserva.getFechaVencimiento());
                }
                break;
            default: throw new IllegalArgumentException("Entidad no válida.");
        }
    }

    private void actualizar(String entidad) {
        boolean actualizado;
        switch (entidad) {
            case "1":
                actualizado = crud.actualizarStand(leerTexto("Identificador: "),
                        EstadoStand.valueOf(leerTexto("Estado (DISPONIBLE, RESERVADO, OCUPADO, BLOQUEADO): ").toUpperCase()));
                break;
            case "2":
                actualizado = crud.actualizarEditorial(leerEntero("ID: "),
                        leerTexto("Nuevo nombre: "), leerTexto("Nuevo contacto: "));
                break;
            case "3":
                int id = leerEntero("ID de reserva: ");
                Reserva existente = buscarReserva(crud.listarReservas(), id);
                LocalDate fecha = leerFecha("Fecha de reserva (AAAA-MM-DD): ");
                Reserva actualizada = new Reserva(id, existente.getStand(), existente.getEditorial(), fecha,
                        leerFecha("Fecha de vencimiento (AAAA-MM-DD): "),
                        leerFecha("Fecha límite pronto pago (AAAA-MM-DD): "),
                        leerFecha("Fecha límite liquidación (AAAA-MM-DD): "));
                actualizado = crud.actualizarReserva(actualizada);
                break;
            default: throw new IllegalArgumentException("Entidad no válida.");
        }
        System.out.println(actualizado ? "Registro actualizado." : "No se encontró el registro.");
    }

    private void eliminar(String entidad) {
        boolean eliminado;
        switch (entidad) {
            case "1": eliminado = crud.eliminarStand(leerTexto("Identificador: ")); break;
            case "2": eliminado = crud.eliminarEditorial(leerEntero("ID: ")); break;
            case "3": eliminado = crud.eliminarReserva(leerEntero("ID de reserva: ")); break;
            default: throw new IllegalArgumentException("Entidad no válida.");
        }
        System.out.println(eliminado ? "Registro eliminado." : "No se encontró el registro.");
    }

    private Stand buscarStand(List<Stand> stands, String id) {
        for (Stand stand : stands) if (stand.getIdentificador().equals(id)) return stand;
        throw new IllegalArgumentException("No existe ese stand en la base de datos.");
    }

    private Editorial buscarEditorial(List<Editorial> editoriales, int id) {
        for (Editorial editorial : editoriales) if (editorial.getId() == id) return editorial;
        throw new IllegalArgumentException("No existe esa editorial en la base de datos.");
    }

    private Reserva buscarReserva(List<Reserva> reservas, int id) {
        for (Reserva reserva : reservas) if (reserva.getId() == id) return reserva;
        throw new IllegalArgumentException("No existe esa reserva en la base de datos.");
    }

    private void configurar() {
        sistema.getConfiguracion().actualizar(
                leerDecimal("Precio por m²: "),
                leerEntero("Días de vigencia de reserva: "),
                leerEntero("Días máximos para liquidar: "),
                leerEntero("Días de pronto pago: "),
                leerDecimal("Porcentaje mínimo para ocupar: "),
                leerDecimal("Porcentaje de descuento: "),
                leerEnteroNoNegativo("Días de anticipación de avisos: "));
        System.out.println("Configuración guardada.");
    }

    private void registrarStand() {
        Stand stand = sistema.registrarStand(
                leerTexto("Identificador: "),
                leerDecimal("Ancho en metros: "),
                leerDecimal("Largo en metros: "));
        System.out.printf(
                "Stand registrado: %s | Área: %s m² | Precio: $%s%n",
                stand.getIdentificador(),
                stand.metrosCuadrados(),
                stand.getPrecio());
    }

    private void mostrarStands() {
        if (sistema.getStands().isEmpty()) {
            System.out.println("No hay stands registrados.");
            return;
        }
        for (Stand stand : sistema.getStands()) {
            System.out.printf(
                    "%s | %s | %s m² | $%s%n",
                    stand.getIdentificador(),
                    stand.getEstado(),
                    stand.metrosCuadrados(),
                    stand.getPrecio());
            if (stand.getReserva() != null) {
                Reserva reserva = stand.getReserva();
                System.out.printf(
                        "  Editorial: %s | Pagado: $%s | Saldo: $%s%n",
                        reserva.getEditorial().getNombre(),
                        reserva.pagado(),
                        reserva.saldo(LocalDate.now(), sistema.getConfiguracion()));
            }
        }
    }

    private void reservar(boolean transferencia) {
        String identificador = leerTexto("Identificador del stand: ");
        Editorial editorial = new Editorial(
                leerTexto("Editorial: "),
                leerTexto("Contacto: "));
        LocalDate fecha = leerFecha("Fecha de reserva (AAAA-MM-DD): ");
        Reserva reserva = transferencia
                ? sistema.transferirBloqueadoAReserva(identificador, editorial, fecha)
                : sistema.reservar(identificador, editorial, fecha);
        System.out.println("Reserva creada. Vence: " + reserva.getFechaVencimiento());
    }

    private void registrarPago() {
        sistema.registrarPago(
                leerTexto("Identificador del stand: "),
                leerDecimal("Importe: "),
                leerFecha("Fecha del pago (AAAA-MM-DD): "));
        System.out.println("Pago registrado correctamente.");
    }

    private void mostrarNotificaciones() {
        java.util.List<Notificacion> notificaciones = sistema.revisarNotificaciones(LocalDate.now());
        if (notificaciones.isEmpty()) {
            System.out.println("No hay notificaciones nuevas.");
            return;
        }
        for (Notificacion notificacion : notificaciones) {
            System.out.println("NOTIFICACIÓN: " + notificacion.getMensaje());
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        String valor = entrada.nextLine().trim();
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El valor es obligatorio.");
        }
        return valor;
    }

    private BigDecimal leerDecimal(String mensaje) {
        try {
            return new BigDecimal(leerTexto(mensaje));
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Ingrese un número válido.");
        }
    }

    private int leerEntero(String mensaje) {
        int valor = leerEnteroNoNegativo(mensaje);
        if (valor <= 0) {
            throw new IllegalArgumentException("Ingrese un entero mayor que cero.");
        }
        return valor;
    }

    private int leerEnteroNoNegativo(String mensaje) {
        try {
            int valor = Integer.parseInt(leerTexto(mensaje));
            if (valor < 0) {
                throw new IllegalArgumentException("Ingrese un entero no negativo.");
            }
            return valor;
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Ingrese un entero válido.");
        }
    }

    private LocalDate leerFecha(String mensaje) {
        try {
            return LocalDate.parse(leerTexto(mensaje));
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException("La fecha debe tener formato AAAA-MM-DD.");
        }
    }
}

# PD4

Aplicación de consola en Java para administrar stands de la Feria Internacional de la Lectura Yucatán (FILEY). Permite configurar costos y plazos, registrar espacios, reservarlos para editoriales, controlar pagos y consultar notificaciones.

## Actores

- **Administrador:** configura las reglas de operación, registra stands, bloquea o desbloquea espacios y consulta su estado.
- **Personal de ventas:** registra reservas y pagos de las editoriales y revisa sus saldos y notificaciones.

## Historias de usuario

### HU-01: Configurar costos y plazos

Como administrador, quiero configurar el precio por metro cuadrado, los plazos de reserva y liquidación y los porcentajes de pago y descuento, para adaptar el sistema a las condiciones de la feria.

**Criterios de aceptación:** los importes deben ser positivos, los porcentajes deben estar entre 0 y 100 y el plazo de liquidación debe cubrir la vigencia de la reserva.

### HU-02: Registrar un stand

Como administrador, quiero registrar un stand con identificador y dimensiones, para conocer automáticamente su área y precio.

**Criterios de aceptación:** el identificador no puede estar vacío ni repetido; las dimensiones deben ser positivas; el sistema calcula el área y multiplica por el precio configurado por metro cuadrado.

### HU-03: Consultar disponibilidad

Como personal de ventas, quiero ver los stands y su estado, para ofrecer únicamente los espacios disponibles y dar seguimiento a los reservados u ocupados.

**Criterios de aceptación:** el listado muestra identificador, estado, área y precio; si existe una reserva, también muestra la editorial, lo pagado y el saldo.

### HU-04: Reservar un stand

Como personal de ventas, quiero asociar un stand disponible con una editorial, para apartar el espacio durante el plazo configurado.

**Criterios de aceptación:** el stand debe existir y estar disponible; se solicitan nombre y contacto de la editorial y fecha de reserva; el sistema calcula y muestra la fecha de vencimiento.

### HU-05: Controlar espacios bloqueados

Como administrador, quiero bloquear o desbloquear stands y convertir un stand bloqueado en una reserva, para gestionar espacios que requieren atención administrativa.

**Criterios de aceptación:** solo se bloquean stands disponibles; solo se desbloquean stands bloqueados; la opción de transferencia acepta únicamente un stand bloqueado.

### HU-06: Registrar pagos

Como personal de ventas, quiero registrar pagos de una reserva, para actualizar el saldo y el estado del stand.

**Criterios de aceptación:** el stand debe tener una reserva, el pago debe ser positivo y no puede superar el saldo pendiente; se aplica el descuento de pronto pago cuando corresponda; al alcanzar el porcentaje mínimo configurado, el stand cambia a ocupado.

### HU-07: Revisar notificaciones

Como personal de ventas, quiero consultar avisos de vencimiento de reservas, descuentos y liquidaciones, para dar seguimiento oportuno a las editoriales.

**Criterios de aceptación:** se muestran avisos dentro del periodo de anticipación configurado y el mismo evento no se repite en revisiones posteriores.

## Casos de uso

### CU-01: Registrar stand

- **Actor:** administrador.
- **Precondición:** el sistema está iniciado y existe una configuración válida de precio por metro cuadrado.
- **Flujo principal:** el actor elige **Registrar stand**, captura identificador, ancho y largo; el sistema valida los datos, calcula área y precio y confirma el registro.
- **Alternativas:** si falta el identificador, una dimensión no es positiva o el identificador ya existe, el sistema informa el error y no registra el stand.
- **Postcondición:** el stand queda disponible en el sistema.

### CU-02: Reservar stand

- **Actor:** personal de ventas.
- **Precondición:** existe un stand disponible.
- **Flujo principal:** el actor elige **Reservar stand**, captura el identificador, nombre y contacto de la editorial y fecha; el sistema crea la reserva y muestra su vencimiento.
- **Alternativas:** si el stand no existe o no está disponible, el sistema informa el error y no crea la reserva.
- **Postcondición:** el stand queda reservado y asociado a la editorial.

### CU-03: Registrar pago

- **Actor:** personal de ventas.
- **Precondición:** existe una reserva para el stand.
- **Flujo principal:** el actor elige **Registrar pago**, captura identificador, importe y fecha; el sistema valida el importe y fecha, registra el pago y actualiza el estado si se alcanza el porcentaje mínimo.
- **Alternativas:** si no hay reserva, el pago no es positivo, excede el saldo o rebasa la fecha límite de liquidación, el sistema informa el error.
- **Postcondición:** el pago queda asociado a la reserva y el saldo puede consultarse en el listado.

### CU-04: Revisar notificaciones

- **Actor:** administrador o personal de ventas.
- **Precondición:** hay reservas registradas.
- **Flujo principal:** el actor elige **Revisar notificaciones**; el sistema compara las fechas límite con la fecha actual y muestra avisos próximos dentro del periodo configurado.
- **Alternativa:** si no hay avisos nuevos, el sistema lo indica; no repite eventos ya notificados.
- **Postcondición:** los avisos nuevos quedan marcados internamente para evitar duplicados.

## Abstracción y responsabilidades

El modelo representa conceptos y reglas del dominio FILEY, mientras la interfaz de consola se encarga de la interacción. Esta separación permite modificar el menú sin trasladar las reglas del negocio a la interfaz.

| Clase | Responsabilidad |
|---|---|
| `Main` | Abrir la conexión, iniciar las lecturas JDBC y después iniciar el menú. |
| `Consola` | Mostrar el menú, leer y validar entradas de formato, invocar operaciones y presentar resultados. |
| `SistemaFiley` | Coordinar configuración, stands, reservas, pagos y revisión de notificaciones. |
| `ControladorCRUD` | Recibir las acciones del menú y llamar a las operaciones JDBC. |
| `ControladorLecturas` | Coordinar las consultas del repositorio y la presentación de las listas. |
| `VistaListas` | Mostrar en consola el estado de conexión y las listas leídas. |
| `ConexionBD` | Abrir la conexión JDBC con H2 en memoria. |
| `InicializadorBD` | Crear las tablas y cargar datos de ejemplo para la demostración. |
| `InsertQuery` | Ejecutar los `INSERT` para stands, editoriales y reservas. |
| `ReadQuery` | Recibir la conexión JDBC y ejecutar los `SELECT` de stands, editoriales y reservas. |
| `UpdateQuery` | Ejecutar los `UPDATE` de stands, editoriales y reservas. |
| `DeleteQuery` | Ejecutar los `DELETE` de stands, editoriales y reservas. |
| `DataAccessException` | Informar errores de consulta JDBC sin propagarlos como `SQLException` al controlador. |
| `DatabaseConnectionException` | Informar errores al abrir o cerrar la conexión. |
| `DatabaseInitializationException` | Informar errores al crear tablas o cargar datos iniciales. |
| `Stand` | Representar un espacio, calcular área y precio y controlar sus cambios de estado. |
| `Reserva` | Asociar stand y editorial, calcular fechas límite, pagos, descuento, saldo y porcentaje pagado. |
| `Editorial` | Representar el nombre y contacto de la editorial. |
| `Pago` | Representar el importe y la fecha de un pago. |
| `Configuracion` | Mantener y validar costos, plazos y porcentajes del sistema. |
| `ServicioNotificaciones` | Detectar vencimientos próximos y evitar notificar dos veces el mismo evento. |
| `Notificacion` | Representar una notificación con clave, mensaje y fecha. |
| `EstadoStand` | Enumerar los estados posibles de un stand. |

## Diagrama de clases


![Diagrama de clases](docs/DiagramaClasesPD4.png)

Flujo de conexión y separación de responsabilidades para las operaciones JDBC disponibles desde el menú:

```mermaid
classDiagram
    class Main
    class ConexionBD
    class Consola
    class ControladorCRUD
    class ControladorLecturas
    class InsertQuery
    class ReadQuery
    class UpdateQuery
    class DeleteQuery
    class InicializadorBD
    class VistaListas
    Main --> ConexionBD : abre y mantiene
    Main --> InicializadorBD : prepara esquema
    Main --> Consola : inicia con controladores
    Main --> ControladorCRUD
    Main --> ControladorLecturas
    Consola --> ControladorCRUD : CRUD interactivo
    ControladorCRUD --> InsertQuery
    ControladorCRUD --> ReadQuery
    ControladorCRUD --> UpdateQuery
    ControladorCRUD --> DeleteQuery
    ControladorLecturas --> ReadQuery
    ControladorLecturas --> VistaListas
    InsertQuery --> ConexionBD : usa Connection
    ReadQuery --> ConexionBD : usa Connection
    UpdateQuery --> ConexionBD : usa Connection
    DeleteQuery --> ConexionBD : usa Connection
```

Los requisitos funcionales y no funcionales completos están en [`requisitos.md`](requisitos.md).

## Diagrama de Entidad-Relación

![Diagrama entidad-relación](docs/Diagrama-Entidad-Relacion.drawio.png)


## Video de evidencia

[Ver video de evidencia](https://drive.google.com/file/d/1j-tsnD6yxnt3t0eF8tWwBzfQj3z_CElO/view)

## Compilar y ejecutar

Requisitos: JDK 11 o posterior y Maven instalado. Maven descarga el controlador H2 al compilar.

Desde la raíz del repositorio, ejecuta:

```bash
mvn compile exec:java
```


Al iniciar, `Main` se conecta a una base H2 temporal, prepara tablas y datos de demostración, y muestra las listas de stands, editoriales y reservas mediante consultas JDBC. Después inicia el menú de consola.

La base H2 vive en memoria y se elimina al cerrar la aplicación. Al iniciar, se cargan datos de ejemplo. La conexión permanece abierta mientras se usa el menú. La opción **10. CRUD de base de datos** ejecuta operaciones JDBC persistidas durante esa ejecución; las opciones originales de administración y pagos siguen trabajando con el modelo de negocio en memoria.

`ConexionBD` abre la conexión. `Main` construye los controladores CRUD y de lecturas y conserva la sesión hasta salir. Cada clase de acceso a datos recibe esa conexión y concentra un tipo de operación: `InsertQuery` (`INSERT`), `ReadQuery` (`SELECT`), `UpdateQuery` (`UPDATE`) y `DeleteQuery` (`DELETE`). `ControladorCRUD` coordina estas clases; la consola solicita los datos y presenta los resultados.

### Operaciones CRUD disponibles en el menú

En la opción 10 se elige operación y entidad. Las tablas se separan por tipo de operación, para que cada consulta esté localizada en una sola clase:

| Entidad | Crear | Consultar | Actualizar | Eliminar |
|---|---|---|---|---|
| `STAND` | Identificador, dimensiones, precio y estado inicial disponible | Lista de stands | Estado | Por identificador |
| `EDITORIAL` | Nombre y contacto; el sistema asigna el siguiente ID | Lista de editoriales | Nombre y contacto | Por ID |
| `RESERVA` | Stand existente, editorial existente y fechas; el sistema asigna el siguiente ID | Lista con stand y editorial asociados | Fechas de la reserva | Por ID; al eliminarla, el stand queda disponible |

Las claves foráneas impiden eliminar un stand o editorial que todavía esté referenciado por una reserva. Para eliminar esos registros, primero se elimina la reserva. Al crear una reserva, el estado del stand cambia a `RESERVADO`.

El menú está escrito con estructuras básicas de Java: `if`, `switch`, ciclos `while` y `for`, y métodos pequeños para cada operación. `ControladorCRUD` coordina las consultas y la consola lee los datos y muestra el resultado.

### Excepciones de acceso a datos

Las excepciones se encuentran en `com.filey.exception` y heredan de `FileyException` (`RuntimeException`):

| Excepción | Dónde se dispara | Argumentos |
|---|---|---|
| `DatabaseConnectionException` | `ConexionBD.conectar()` si falla la apertura y `close()` si falla el cierre | Mensaje contextual (`String`) y causa JDBC (`SQLException`) |
| `DatabaseInitializationException` | `InicializadorBD.crearTablasYDatos()` si falla el DDL o la carga de datos iniciales | Mensaje contextual (`String`) y causa JDBC (`SQLException`) |
| `DataAccessException` | Métodos de `InsertQuery`, `ReadQuery`, `UpdateQuery` y `DeleteQuery` si JDBC rechaza la consulta | Mensaje por operación (`String`) y causa JDBC (`SQLException`) |
| `IllegalArgumentException` | Validaciones de entidades, entradas de menú y argumentos inválidos de las consultas | Mensaje que describe el campo u opción inválida; no lleva causa |
| `IllegalStateException` | Reglas del dominio, por ejemplo reservar un stand no disponible o pagar después del plazo | Mensaje de la regla incumplida; no lleva causa |

El controlador propaga los errores de acceso; el menú captura las excepciones de dominio y JDBC y muestra su mensaje para que la aplicación pueda continuar.



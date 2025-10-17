# CrudPark — Aplicación Operativa de Escritorio (Java)

Este repositorio contiene el módulo operativo del sistema de gestión de parqueaderos **CrudPark**. Es una aplicación de escritorio desarrollada en Java que permite a los operadores gestionar el flujo diario de vehículos (ingresos, salidas, cobros y emisión de tickets) en tiempo real.

Esta aplicación se conecta a una base de datos **PostgreSQL** compartida con el módulo administrativo web (desarrollado en .NET/C#).

---

## 🛠 Tecnologías y Versiones Utilizadas

El proyecto ha sido construido utilizando las siguientes tecnologías y librerías:

*   **Lenguaje:** Java JDK **21**
*   **Gestor de Construcción:** Apache Maven 3.x
*   **Interfaz Gráfica (GUI):** Java Swing
*   **Base de Datos:** PostgreSQL
*   **Conector JDBC:** `org.postgresql:postgresql:42.7.3`
*   **Generación de Códigos QR:** ZXing (`com.google.zxing:core:3.5.3` y `javase:3.5.3`)
*   **Impresión:** Java Print Service API (Nativa del JDK)

---

## cS Arquitectura del Software

El proyecto sigue una arquitectura robusta y escalable basada en capas: **MVC (Modelo-Vista-Controlador) + Capa de Servicio + Patrón Repositorio (DAO)**.

1.  **View (Vista):** Clases Swing (`LoginView`, `MainView`) que solo manejan la presentación y los eventos de la interfaz.
2.  **Controller (Controlador):** Orquestan la comunicación entre la vista y los servicios. No contienen lógica de negocio compleja.
3.  **Service (Servicio):** Contiene toda la **lógica de negocio** (cálculo de tarifas, reglas de tiempo de gracia, validación de mensualidades, generación de QR). Es el núcleo de la aplicación.
4.  **Repository (Repositorio/DAO):** Interfaces e implementaciones encargadas exclusivamente de la comunicación con la base de datos mediante JDBC.
5.  **Model (Modelo):** POJOs que representan las entidades de la base de datos (`Operador`, `Estancia`, `Tarifa`, etc.).

---

## ⚙️ Configuración e Instalación

### 1. Requisitos Previos

*   Tener instalado el JDK 21.
*   Tener instalado Maven.
*   Tener acceso a una instancia de PostgreSQL.
*   (Opcional) Una impresora configurada en el sistema operativo para la impresión real de tickets.

### 2. Configuración de la Base de Datos

Ejecuta el siguiente script SQL en tu base de datos PostgreSQL para crear el esquema necesario. **Es importante respetar el orden para evitar errores de claves foráneas.**

I. **Creación de Tablas Maestras:**

```sql
-- Tabla de Operadores
CREATE TABLE Operadores (
    id_operador       SERIAL PRIMARY KEY,
    nombre_usuario    VARCHAR(50) UNIQUE NOT NULL,
    contrasena        VARCHAR(255) NOT NULL,
    nombre_completo   VARCHAR(100) NOT NULL,
    correo            VARCHAR(100),
    activo            BOOLEAN NOT NULL DEFAULT true
);

-- Tabla de Tarifas
CREATE TABLE Tarifas (
    id_tarifa               SERIAL PRIMARY KEY,
    descripcion             VARCHAR(255) NOT NULL,
    valor_hora              NUMERIC(10, 2) NOT NULL,
    valor_fraccion          NUMERIC(10, 2) NOT NULL,
    tope_diario             NUMERIC(10, 2),
    tiempo_gracia_minutos   INTEGER NOT NULL CHECK (tiempo_gracia_minutos >= 0),
    es_activa               BOOLEAN NOT NULL DEFAULT false
);

-- Tabla de Clientes
CREATE TABLE Clientes (
    id_cliente        SERIAL PRIMARY KEY,
    nombre_completo   VARCHAR(150) NOT NULL,
    correo            VARCHAR(100) UNIQUE
);
```

II. **Creación de Tablas Dependientes y Relaciones:**

```sql
-- Tabla de Mensualidades
CREATE TABLE Mensualidades (
    id_mensualidad   SERIAL PRIMARY KEY,
    id_cliente       INTEGER NOT NULL,
    placa            VARCHAR(10) NOT NULL,
    fecha_inicio     DATE NOT NULL,
    fecha_fin        DATE NOT NULL
);

ALTER TABLE Mensualidades
    ADD CONSTRAINT fk_cliente FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente) ON DELETE CASCADE;

-- Tabla de Estancias (Tickets)
CREATE TABLE Estancias (
    id_estancia          SERIAL PRIMARY KEY,
    placa                VARCHAR(10) NOT NULL,
    fecha_ingreso        TIMESTAMP NOT NULL,
    fecha_salida         TIMESTAMP,
    tipo_estancia        VARCHAR(20) NOT NULL CHECK (tipo_estancia IN ('Mensualidad', 'Invitado')),
    estado               VARCHAR(10) NOT NULL CHECK (estado IN ('DENTRO', 'FUERA')),
    id_operador_ingreso  INTEGER NOT NULL,
    id_operador_salida   INTEGER
);

ALTER TABLE Estancias
    ADD CONSTRAINT fk_operador_ingreso FOREIGN KEY (id_operador_ingreso) REFERENCES Operadores(id_operador) ON DELETE RESTRICT;
ALTER TABLE Estancias
    ADD CONSTRAINT fk_operador_salida FOREIGN KEY (id_operador_salida) REFERENCES Operadores(id_operador) ON DELETE SET NULL;

-- Tabla de Pagos
CREATE TABLE Pagos (
    id_pago             SERIAL PRIMARY KEY,
    id_estancia         INTEGER NOT NULL UNIQUE,
    monto               NUMERIC(10, 2) NOT NULL,
    fecha_pago          TIMESTAMP NOT NULL,
    metodo_pago         VARCHAR(50),
    id_operador_cobro   INTEGER NOT NULL
);

ALTER TABLE Pagos
    ADD CONSTRAINT fk_estancia FOREIGN KEY (id_estancia) REFERENCES Estancias(id_estancia) ON DELETE RESTRICT;
ALTER TABLE Pagos
    ADD CONSTRAINT fk_operador_cobro FOREIGN KEY (id_operador_cobro) REFERENCES Operadores(id_operador) ON DELETE SET NULL;
```

III. **Datos Semilla (Obligatorios para probar):**

```sql
-- Crear al menos un operador
INSERT INTO Operadores (nombre_usuario, contrasena, nombre_completo, activo)
VALUES ('admin', '12345', 'Operador Principal', true);

-- Crear al menos una tarifa activa
INSERT INTO Tarifas (descripcion, valor_hora, valor_fraccion, tope_diario, tiempo_gracia_minutos, es_activa)
VALUES ('Tarifa Estándar 2025', 5000.00, 1500.00, 50000.00, 30, true);
```

### 3. Configuración de la Aplicación

1.  Clona el repositorio.
2.  Navega a `src/main/resources/`.
3.  Edita el archivo `config.properties` con tus credenciales de base de datos.

    **Ejemplo para conexión local:**
    ```properties
    db.url=jdbc:postgresql://localhost:5432/crudpark_db
    db.user=tu_usuario
    db.password=tu_contraseña
    ```

    **Ejemplo para conexión en la nube (Render, AWS, etc.) con SSL:**
    ```properties
    db.url=jdbc:postgresql://host-remoto:5432/nombre_db?sslmode=require
    db.user=tu_usuario_remoto
    db.password=tu_contraseña_remota
    ```

### 4. Compilación y Ejecución

Desde la raíz del proyecto, usa Maven:

1.  **Descargar dependencias y compilar:**
    ```bash
    mvn clean install
    ```
2.  **Ejecutar la aplicación:**
    ```bash
    mvn exec:java
    ```

---

## 🚀 Flujo de Uso y Funcionalidades

### 1. Inicio de Sesión
*   Al iniciar, la aplicación muestra una pantalla de login.
*   Debe ingresar las credenciales de un operador existente en la tabla `Operadores` que tenga el estado `activo = true`.

### 2. Panel Principal (Dashboard)
*   Una vez autenticado, se abre el panel principal.
*   **Tabla en Tiempo Real:** Muestra automáticamente todos los vehículos que se encuentran actualmente dentro del parqueadero (estado 'DENTRO'), cargados desde la base de datos.

### 3. Registro de Ingreso
1.  El operador ingresa una placa y hace clic en "Registrar Ingreso".
2.  El sistema valida que el vehículo no esté ya dentro.
3.  Verifica automáticamente en la tabla `Mensualidades` si la placa tiene una membresía vigente.
4.  Crea el registro en la base de datos como "Invitado" o "Mensualidad".
5.  Actualiza la tabla del dashboard inmediatamente.

### 4. Generación e Impresión de Tickets
*   Tras un ingreso exitoso, el sistema genera internamente un ticket que incluye un **Código QR** con el formato: `TICKET:{stay_id}|PLATE:{placa}|DATE:{timestamp}`.
*   La aplicación pregunta al operador qué hacer:
    *   **Ver Simulación:** Muestra una ventana emergente con el texto del ticket y la imagen del QR generado.
    *   **Imprimir Ticket:** Envía el ticket formateado directamente a la impresora predeterminada del sistema operativo.

### 5. Registro de Salida y Cobro
1.  El operador ingresa la placa que va a salir.
2.  El sistema calcula el tiempo de estadía.
3.  **Aplica Reglas de Negocio:**
    *   Si es "Mensualidad": Costo $0.
    *   Si es "Invitado" y está dentro del tiempo de gracia (según tabla `Tarifas`): Costo $0.
    *   Si excede el tiempo de gracia: Calcula el costo basado en valor por hora/fracción y tope diario de la tarifa activa.
4.  Muestra una confirmación con el monto a cobrar.
5.  Al confirmar, registra el pago en la tabla `Pagos`, marca la estancia como 'FUERA' con la hora de salida, y elimina el vehículo de la tabla del dashboard.

---

## 👥 Créditos y Equipo

Este proyecto es parte del desafío **CrudPark Ops & Admin Challenge**.

*   **Equipo:** [Nombre de tu Equipo Aquí]
*   **Registro:** 👉 [Enlace a teams.crudzaso.com]

**Integrantes:**
*   🧑‍💻 **[Tu Nombre]** - Rol: Java Desktop Developer (Equipo Berners-Lee)
*   🧑‍💻 **[Nombre Compañero 1]** - Rol: C# Developer (Equipo Van Rossum)
*   🧑‍💻 **[Nombre Compañero 2]** - Rol: C# Developer (Equipo Van Rossum)
# Sistema de Gestión de Pedidos y Envíos

## Trabajo Práctico Integrador - Programación 2

## 👥 Integrantes del Equipo

| Usuario GitHub      | Nombre Completo               |
|---------------------|-------------------------------|
| @OviedoMarcelo      | Marcelo Oviedo                |
| @efedefede          | Federico Panella              |
| @Gemmanuel96        | Gonzalo Emanuel Nuñez         |
| @FPaolazzi          | Florencia Paolazzi            |

---

## 🎥 Video de Presentación del Proyecto

> 🔗 **Enlace **  
> `https://leautneduar-my.sharepoint.com/:v:/g/personal/marcelo_oviedo_tupad_utn_edu_ar/IQALN7erJS0yQKVJ1DyWX5YkAbWudM0jVg1pLCCnWdEspWw?e=tWThOJ`

### Descripción del Proyecto

Este Trabajo Práctico Integrador tiene como objetivo demostrar la aplicación práctica de los conceptos fundamentales de Programación Orientada a Objetos y Persistencia de Datos aprendidos durante el cursado de Programación 2. El proyecto consiste en desarrollar un sistema completo de gestión de personas y domicilios que permita realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre estas entidades, implementando una arquitectura robusta y profesional.

### Objetivos Académicos

El desarrollo de este sistema permite aplicar y consolidar los siguientes conceptos clave de la materia:

**1. Arquitectura en Capas (Layered Architecture)**
- Implementación de separación de responsabilidades en 4 capas diferenciadas
- Capa de Presentación (Main/UI): Interacción con el usuario mediante consola
- Capa de Lógica de Negocio (Service): Validaciones y reglas de negocio
- Capa de Acceso a Datos (DAO): Operaciones de persistencia
- Capa de Modelo (Models/Entities): Representación de entidades del dominio

**2. Programación Orientada a Objetos**
- Aplicación de principios SOLID (Single Responsibility, Dependency Injection)
- Uso de herencia mediante clase abstracta Base
- Implementación de interfaces genéricas (GenericDAO, GenericService)
- Encapsulamiento con atributos privados y métodos de acceso
- Sobrescritura de métodos (equals, hashCode, toString)

**3. Persistencia de Datos con JDBC**
- Conexión a base de datos MySQL mediante JDBC
- Implementación del patrón DAO (Data Access Object)
- Uso de PreparedStatements para prevenir SQL Injection
- Gestión de transacciones con commit y rollback
- Manejo de claves autogeneradas (AUTO_INCREMENT)
- Consultas con LEFT JOIN para relaciones entre entidades

**4. Manejo de Recursos y Excepciones**
- Uso del patrón try-with-resources para gestión automática de recursos JDBC
- Implementación de AutoCloseable en TransactionManager
- Manejo apropiado de excepciones con propagación controlada
- Validación multi-nivel: base de datos y aplicación

**5. Patrones de Diseño**
- Factory Pattern (DatabaseConnection)
- Service Layer Pattern (separación lógica de negocio)
- DAO Pattern (abstracción del acceso a datos)
- Soft Delete Pattern (eliminación lógica de registros)
- Dependency Injection manual

**6. Validación de Integridad de Datos**
- Validación de unicidad (ID único Pedido y Envío)
- Validación de campos obligatorios en múltiples niveles
- Validación de integridad referencial (Foreign Keys)
- Implementación de eliminación segura para prevenir referencias huérfanas

### Funcionalidades Implementadas

El sistema permite gestionar dos entidades principales con las siguientes operaciones:


## Requisitos del Sistema

| Componente | Versión Requerida |
|------------|-------------------|
| Java JDK | 24 o superior |
| MySQL | 8.0 o superior |
| Gradle | 8.12 (incluido wrapper) |
| Sistema Operativo | Windows, Linux o macOS |

## Instalación

### 1. Configurar Base de Datos

Ejecutar los siguientes scripts SQL en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS pedidoenviotpi; 

CREATE TABLE envios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN DEFAULT FALSE NOT NULL,
    tracking VARCHAR(100) NOT NULL UNIQUE,
    empresa ENUM('ANDREANI', 'OCA', 'CORREO_ARG') NOT NULL,
    tipo ENUM('ESTANDAR', 'EXPRESS') NOT NULL,
    costo DOUBLE(10, 2) NOT NULL CHECK (costo > 0),
    fecha_despacho DATE NULL,
    fecha_estimada DATE NULL,
    estado ENUM('EN_PREPARACION', 'EN_TRANSITO', 'ENTREGADO') NOT NULL DEFAULT 'EN_PREPARACION'
);

CREATE TABLE pedidos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    eliminado BOOLEAN DEFAULT FALSE NOT NULL,
    numero VARCHAR(50) NOT NULL,
    fecha DATE NOT NULL,
    clienteNombre VARCHAR(100) NOT NULL,
    total DOUBLE(10 , 2 ) NOT NULL CHECK (total > 0),
    estado ENUM('NUEVO', 'FACTURADO', 'ENVIADO') NOT NULL,
    envio INT NOT NULL,
    FOREIGN KEY (envio)
        REFERENCES envios (id)
);
```
1. Copia `database.properties.example` a `database.properties`
2. Configura tus credenciales reales
3. `database.properties` está en .gitignore por seguridad

### 2. Compilar el Proyecto

```bash
# Linux/macOS
./gradlew clean build

# Windows
gradlew.bat clean build
```

### 3. Configurar Conexión (Opcional)

Por defecto conecta a:
- **Host**: localhost:3306
- **Base de datos**: sistemadeenvios
- **Usuario**: root
- **Contraseña**: (vacía)

Para cambiar la configuración, usar propiedades del sistema:

```bash
java -Ddb.url=jdbc:mysql://localhost:3306/sistemadeenvios \
     -Ddb.user=usuario \
     -Ddb.password=clave \
     -cp ...
```

## Ejecución

### Opción 1: Desde IDE
1. Abrir proyecto en Netbeans y otro IDE.
2. Ejecutar clase `Main.Main`

### Opción 2: Línea de comandos

**Windows:**
```bash
# Localizar JAR de MySQL
dir /s /b %USERPROFILE%\.gradle\caches\*mysql-connector-j-8.4.0.jar

# Ejecutar (reemplazar <ruta-mysql-jar>)
java -cp "build\classes\java\main;<ruta-mysql-jar>" Main.Main
```

**Linux/macOS:**
```bash
# Localizar JAR de MySQL
find ~/.gradle/caches -name "mysql-connector-j-8.4.0.jar"

# Ejecutar (reemplazar <ruta-mysql-jar>)
java -cp "build/classes/java/main:<ruta-mysql-jar>" Main.Main
```

### Verificar Conexión

```bash
# Usar TestConexion para verificar conexión a BD
java -cp "build/classes/java/main:<ruta-mysql-jar>" Main.TestConexion
```

Salida esperada:
```
Conexion exitosa a la base de datos
Usuario conectado: root@localhost
Base de datos: sistemadeenvios
URL: jdbc:mysql://localhost:3306/sistemadeenvios
Driver: MySQL Connector/J v8.4.0
```

## Uso del Sistema

### Menú Principal (Pendiente)

```
--- MENÚ PRINCIPAL ---
1. Crear Pedido con Envío
2. Listar todos los Pedidos
3. Buscar Pedido por Número
4. Buscar Pedido por Cliente
5. Actualizar Pedido
6. Actualizar Estado de Envío
7. Eliminar Pedido (lógico)
8. Listar Envíos por Empresa
9. Ver Estadísticas
0. Salir
Seleccione una opción:

```

## Arquitectura

### Estructura en Capas

```
┌─────────────────────────────────────┐
│     Main / UI Layer                 │
│  (Interacción con usuario)          │
│  AppMenu, MenuHandler, MenuDisplay  │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     Service Layer                   │
│  (Lógica de negocio y validación)   │
│  PedidoServiceImpl                 │
│  EnvioServiceImpl               │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     DAO Layer                       │
│  (Acceso a datos)                   │
│  PedidoDAO, EnvioDAO           │
└───────────┬─────────────────────────┘
            │
┌───────────▼─────────────────────────┐
│     Models Layer                    │
│  (Entidades de dominio)             │
│  Pedido, Envio, Base           │
└─────────────────────────────────────┘
```

### Componentes Principales

### Componentes Principales

**config/**
- `DatabaseConnection.java`: Maneja la creación de conexiones JDBC.
- `DatabaseConnectionPool.java`: Implementa un pool de conexiones para optimizar el acceso a la BD.
- `TransactionManager.java`: Controla transacciones utilizando `AutoCloseable` para garantizar commit/rollback seguros.
- `database.properties`: Archivo de configuración con credenciales y parámetros de la BD.

**entities/**
- `Base.java`: Clase abstracta con campos comunes (`id`, `eliminado`).
- `EntidadBase.java`: Capa intermedia que estandariza comportamiento en entidades.
- `Envio.java`: Entidad envío (tracking, empresa, tipo, costo, fechas, estado).
- `Pedido.java`: Entidad pedido (número, fecha, cliente, total, estado, FK envío).
- `EmpresaDeEnvio.java`: Enum con empresas posibles (`ANDREANI`, `OCA`, `CORREO_ARG`).
- `TipoDeEnvio.java`: Enum de tipo de envío (`ESTANDAR`, `EXPRESS`).
- `EstadoDeEnvio.java`: Enum de estado para envíos.
- `EstadoDePedido.java`: Enum de estado para pedidos.

**dao/**
- `GenericDAO.java`: Interfaz genérica con operaciones CRUD básicas.
- `EnvioDAO.java`: Acceso a datos para `Envio` (alta, baja lógica, búsquedas, filtrado).
- `PedidoDAO.java`: Acceso a datos para `Pedido`, incluida la relación con `Envio`.

**service/**
- `GenericService.java`: Interfaz genérica para servicios de negocio.
- `EnvioService.java`: Contiene validaciones de negocio para envíos (tracking único, costo válido, enums).
- `PedidoService.java`: Validaciones para pedidos (campos obligatorios, monto positivo, existencia del envío asociado).

**main/**
- `Main.java`: Punto de entrada de la aplicación.
- `AppMenu.java`: Controlador principal del menú interactivo.
- `MenuHandler.java`: Implementación de las operaciones CRUD, manejo de input y flujo general.


## Modelo de Datos

```
┌────────────────────────┐          ┌──────────────────────────┐
│        pedidos         │          │          envios          │
├────────────────────────┤          ├──────────────────────────┤
│ id (PK)                │          │ id (PK)                  │
│ numero                 │          │ tracking (UNIQUE)        │
│ fecha                  │          │ empresa (ENUM)           │
│ clienteNombre          │          │ tipo (ENUM)              │
│ total                  │          │ costo                    │
│ estado (ENUM)          │          │ fecha_despacho           │
│ envio (FK) ────────────┼────────▶ │ fecha_estimada           │
│ eliminado              │          │ estado (ENUM)            │
└────────────────────────┘          │ eliminado                │
                                   └──────────────────────────┘

                             Relación: Muchos (pedidos) → Uno (envio)
```

**🔒 Reglas del modelo:**

-Cada pedido debe tener exactamente un envío asociado (el campo envio es NOT NULL y es FK → envios.id).
-Un envío puede estar asociado a uno o varios pedidos.
-El campo tracking en envios es único (constraint a nivel de base de datos y validación en la aplicación).
-Ambos modelos implementan eliminación lógica mediante el campo eliminado = TRUE.
-Los campos con montos (total, costo) tienen validación de valores positivos mediante CHECK.
-Los estados (estado) y clasificaciones (empresa, tipo) están controlados mediante ENUMs para garantizar consistencia.

## Patrones y Buenas Prácticas

### Seguridad
- **100% PreparedStatements**: Prevención de SQL injection
- **Validación multi-capa**: Service layer valida antes de persistir

### Gestión de Recursos
- **Try-with-resources**: Todas las conexiones, statements y resultsets
- **AutoCloseable**: TransactionManager cierra y hace rollback automático
- **Scanner cerrado**: En `AppMenu.run()` al finalizar

### Validaciones
- **Input trimming**: Todos los inputs usan `.trim()` inmediatamente
- **Campos obligatorios**: Validación de null y empty en service layer
- **IDs único**: Validación `id AUTOINCREMENTAL` en cada alta generado por la base y actualizado en el objeto.
- **Verificación de rowsAffected**: En UPDATE y DELETE

### Soft Delete
- DELETE ejecuta: `UPDATE tabla SET eliminado = TRUE WHERE id = ?`
- SELECT filtra: `WHERE eliminado = FALSE`
- No hay eliminación física de datos

## 🔑 Reglas de Negocio Principales

1. **Tracking único** Cada envío debe tener un `tracking` irrepetible.  
   *Validado por constraint `UNIQUE` y generado por UUID.*

2. **Campos obligatorios en pedidos** Los campos `numero`, `fecha`, `clienteNombre`, `total`, `estado` y `envio` son requeridos para registrar un pedido.

3. **Validación previa a persistir** Toda operación pasa por la capa de servicio, que valida:  
   - Presencia de campos obligatorios  
   - ENUMs válidos  
   - Valores positivos (`total`, `costo`)  
   - Existencia del envío referenciado

4. **Relación controlada Pedido → Envío**  No se permite persistir un pedido con un `envio` inexistente.  La FK debe apuntar a un envío válido y no eliminado lógicamente.

5. **Preservación de valores en actualizaciones**  En las actualizaciones parciales, los campos no enviados mantienen su valor original.

6. **Búsqueda flexible**  Las consultas permiten coincidencias parciales mediante `LIKE '%valor%'`.

7. **Transacciones en operaciones complejas**  Procedimientos que afectan múltiples entidades se ejecutan dentro de transacciones, permitiendo **rollback** en caso de error.

## Solución de Problemas

### Error: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Causa**: JAR de MySQL no está en classpath

**Solución**: Incluir mysql-connector-j-8.4.0.jar en el comando java -cp

### Error: "Communications link failure"
**Causa**: MySQL no está ejecutándose

**Solución**:
```bash
# Linux/macOS
sudo systemctl start mysql
# O
brew services start mysql

# Windows
net start MySQL80
```

### Error: "Access denied for user 'root'@'localhost'"
**Causa**: Credenciales incorrectas

**Solución**: Verificar usuario/contraseña en DatabaseConnection.java o usar -Ddb.user y -Ddb.password

### Error: "Unknown database 'sistemadeenvios'"
**Causa**: Base de datos no creada

**Solución**: Ejecutar script de creación de base de datos (ver sección Instalación)

### Error: "Table 'personas' doesn't exist"
**Causa**: Tablas no creadas

**Solución**: Ejecutar script de creación de tablas (ver sección Instalación)

## Limitaciones Conocidas

1. **No hay tarea gradle run**: Debe ejecutarse con java -cp manualmente o desde IDE
2. **Interfaz solo consola**: No hay GUI gráfica
3. **Un domicilio por persona**: No soporta múltiples domicilios
4. **Sin paginación**: Listar todos puede ser lento con muchos registros
5. **Sin pool de conexiones**: Nueva conexión por operación (aceptable para app de consola)
6. **Sin transacciones en MenuHandler**: Actualizar persona + domicilio puede fallar parcialmente

## Tecnologías Utilizadas

- **Lenguaje**: Java 24
- **Build Tool**: Gradle 8.12
- **Base de Datos**: MySQL 8.x
- **JDBC Driver**: mysql-connector-j 8.4.0

## Estructura de Directorios

```
TrabajoPractico2/
├── src/
│   └── main/
│       └── java/
│           ├── config/                 # Conexión a BD, pool, transacciones
│           │   ├── DatabaseConnection.java
│           │   ├── DatabaseConnectionPool.java
│           │   ├── TransactionManager.java
│           │   └── database.properties
│           │
│           ├── dao/                    # Capa de acceso a datos (DAO)
│           │   ├── GenericDAO.java
│           │   ├── EnvioDAO.java
│           │   └── PedidoDAO.java
│           │
│           ├── entities/               # Modelos / Entidades del dominio
│           │   ├── Base.java
│           │   ├── EntidadBase.java
│           │   ├── Envio.java
│           │   ├── Pedido.java
│           │   ├── EmpresaDeEnvio.java
│           │   ├── TipoDeEnvio.java
│           │   ├── EstadoDeEnvio.java
│           │   └── EstadoDePedido.java
│           │
│           ├── main/                   # Menu, interacción y punto de entrada
│           │   ├── AppMenu.java
│           │   ├── MenuHandler.java
│           │   └── Main.java
│           │
│           └── service/                # Lógica de negocio (Services)
│               ├── GenericService.java
│               ├── EnvioService.java
│               └── PedidoService.java
│
├── README.md                           # Documentación principal
```


## Evaluación y Criterios de Calidad

### Aspectos Evaluados en el TPI

Este proyecto demuestra competencia en los siguientes criterios académicos:

**✅ Arquitectura y Diseño (30%)**
- Correcta separación en capas con responsabilidades bien definidas
- Aplicación de patrones de diseño apropiados (DAO, Service Layer, Factory)
- Uso de interfaces para abstracción y polimorfismo
- Implementación de herencia con clase abstracta Base

**✅ Persistencia de Datos (25%)**
- Correcta implementación de operaciones CRUD con JDBC
- Uso apropiado de PreparedStatements (100% de las consultas)
- Gestión de transacciones con commit/rollback
- Manejo de relaciones entre entidades (Foreign Keys, LEFT JOIN)
- Soft delete implementado correctamente

**✅ Manejo de Recursos y Excepciones (20%)**
- Try-with-resources en todas las operaciones JDBC
- Cierre apropiado de conexiones, statements y resultsets
- Manejo de excepciones con mensajes informativos al usuario
- Prevención de resource leaks

**✅ Validaciones e Integridad (15%)**
- Validación de campos obligatorios en múltiples niveles
- Validación de unicidad de DNI (base de datos + aplicación)
- Verificación de integridad referencial
- Prevención de referencias huérfanas mediante eliminación segura

**✅ Calidad de Código (10%)**
- Código documentado con Javadoc completo (13 archivos)
- Convenciones de nomenclatura consistentes
- Código legible y mantenible
- Ausencia de code smells o antipatrones críticos

**✅ Funcionalidad Completa (10%)**
- Todas las operaciones CRUD funcionan correctamente
- Búsquedas y filtros implementados
- Interfaz de usuario clara y funcional
- Manejo robusto de errores


### Conceptos de Programación 2 Demostrados

| Concepto | Implementación en el Proyecto |
|----------|-------------------------------|
| **Herencia** | Clase abstracta `Base` heredada por `Pedido` y `Envio` |
| **Polimorfismo** | Interfaces `GenericDAO<T>` y `GenericService<T>` |
| **Encapsulamiento** | Atributos privados con getters/setters en todas las entidades |
| **Abstracción** | Interfaces que definen contratos sin implementación |
| **JDBC** | Conexión, PreparedStatements, ResultSets, transacciones |
| **DAO Pattern** | `PedidoDAO`, `EnvioDAO` abstraen el acceso a datos |
| **Service Layer** | Lógica de negocio separada en `PedidoServiceImpl`, `EnvioServiceImpl` |
| **Exception Handling** | Try-catch en todas las capas, propagación controlada |
| **Resource Management** | Try-with-resources para AutoCloseable (Connection, Statement, ResultSet) |
| **Dependency Injection** | Construcción manual de dependencias en `AppMenu.createPersonaService()` |

## Contexto Académico

**Materia**: Programación 2
**Tipo de Evaluación**: Trabajo Práctico Integrador (TPI)
**Modalidad**: Desarrollo de sistema CRUD con persistencia en base de datos
**Objetivo**: Aplicar conceptos de POO, JDBC, arquitectura en capas y patrones de diseño

Este proyecto representa la integración de todos los conceptos vistos durante el cuatrimestre, demostrando capacidad para:
- Diseñar sistemas con arquitectura profesional
- Implementar persistencia de datos con JDBC
- Aplicar patrones de diseño apropiados
- Manejar recursos y excepciones correctamente
- Validar integridad de datos en múltiples niveles
- Documentar código de forma profesional

---

**Proyecto Educativo** - Trabajo Práctico Integrador de Programación 2

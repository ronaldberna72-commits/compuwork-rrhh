# Sistema de Gestión de RRHH - CompuWork (v2.0)

Aplicación de escritorio en Java y JavaFX diseñada para la administración integral de empleados (permanentes y temporales), gestión de departamentos, cálculo polimórfico de salarios, persistencia local y generación/exportación de reportes.

## Características
- **CRUD completo de empleados:** Crear, visualizar, editar y eliminar empleados permanentes y temporales (herencia y polimorfismo).
- **Gestión de departamentos:** Creación dinámica de nuevos departamentos desde la interfaz y asignación de personal.
- **Buscador y filtros en tiempo real:** Filtrado instantáneo por nombre, apellido, cédula (CC) y departamento.
- **Persistencia automática en CSV:** Los departamentos y empleados se guardan automáticamente en la carpeta local `datos/` y se recargan al iniciar la aplicación.
- **Reportes y exportación:** Generación de reportes individuales o departamentales con opción de exportar a archivo `.txt`.
- **Pruebas unitarias automatizadas:** Suite completa con JUnit 5 (modelo y persistencia).

## Requisitos
- **Java 17 (LTS)** o superior
- **Maven 3.6** o superior
- **JavaFX 17** (descargado automáticamente por Maven)

## Estructura del Proyecto
```text
compuwork-rrhh/
├── .gitignore
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/compuwork/
    │   │   ├── Main.java
    │   │   ├── controlador/MainController.java
    │   │   ├── modelo/
    │   │   │   ├── Departamento.java
    │   │   │   ├── Empleado.java
    │   │   │   ├── EmpleadoPermanente.java
    │   │   │   └── EmpleadoTemporal.java
    │   │   └── persistencia/PersistenciaCSV.java
    │   └── resources/vista/main.fxml
    └── test/
        └── java/com/compuwork/
            ├── modelo/
            │   ├── DepartamentoTest.java
            │   └── EmpleadoTest.java
            └── persistencia/
                └── PersistenciaCSVTest.java
```

## Ejecución y Pruebas

### Ejecutar pruebas unitarias
```bash
mvn clean test
```

### Ejecutar la aplicación gráfica
```bash
mvn javafx:run
```

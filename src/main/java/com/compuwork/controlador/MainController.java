package com.compuwork.controlador;

import com.compuwork.modelo.Departamento;
import com.compuwork.modelo.Empleado;
import com.compuwork.modelo.EmpleadoPermanente;
import com.compuwork.modelo.EmpleadoTemporal;
import com.compuwork.persistencia.PersistenciaCSV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador principal de la interfaz gráfica ({@code main.fxml}).
 * Soporta CRUD completo de empleados, creación de departamentos, búsqueda y filtros,
 * persistencia automática en CSV y exportación de reportes a TXT.
 */
public class MainController {

    private static final Departamento OPCION_TODOS = new Departamento(-1, "Todos los departamentos");

    private final PersistenciaCSV persistencia = new PersistenciaCSV(Path.of("datos"));
    private final ObservableList<Departamento> departamentos = FXCollections.observableArrayList();
    private final ObservableList<Empleado> empleados = FXCollections.observableArrayList();
    private FilteredList<Empleado> empleadosFiltrados;

    @FXML private ComboBox<Departamento> comboDepartamentos;
    @FXML private ComboBox<Departamento> comboFiltroDepartamento;
    @FXML private TextField txtBuscar;
    @FXML private ListView<Empleado> listaEmpleados;
    @FXML private TextArea areaReporte;

    @FXML private TextField txtNombre, txtApellido, txtCc;
    @FXML private DatePicker fechaContratacion;
    @FXML private TextField txtSalarioBase;
    @FXML private RadioButton rbPermanente, rbTemporal;
    @FXML private VBox panelPermanente, panelTemporal;
    @FXML private TextField txtBonificacionAntiguedad, txtBonificacionProyecto;
    @FXML private DatePicker fechaFinContrato;

    @FXML
    public void initialize() {
        ToggleGroup grupo = new ToggleGroup();
        rbPermanente.setToggleGroup(grupo);
        rbTemporal.setToggleGroup(grupo);

        panelPermanente.managedProperty().bind(panelPermanente.visibleProperty());
        panelTemporal.managedProperty().bind(panelTemporal.visibleProperty());

        rbPermanente.selectedProperty().addListener((obs, oldVal, newVal) -> {
            panelPermanente.setVisible(newVal);
            panelTemporal.setVisible(!newVal);
        });

        cargarDatosIniciales();
        configurarFiltrosYSeleccion();
    }

    private void cargarDatosIniciales() {
        try {
            if (persistencia.existenDatosGuardados()) {
                List<Departamento> deptosCargados = persistencia.cargarDepartamentos();
                List<Empleado> empsCargados = persistencia.cargarEmpleados(deptosCargados);
                departamentos.setAll(deptosCargados);
                empleados.setAll(empsCargados);
            } else {
                inicializarDatosEjemplo();
                guardarCambios();
            }
        } catch (IOException e) {
            mostrarError("No se pudieron cargar los datos guardados. Se usarán datos de ejemplo.\n" + e.getMessage());
            inicializarDatosEjemplo();
        }
        actualizarCombosDepartamentos();
    }

    private void inicializarDatosEjemplo() {
        Departamento rrhh = new Departamento(1, "Recursos Humanos");
        Departamento ti = new Departamento(2, "Tecnología de la Información");
        departamentos.addAll(rrhh, ti);

        EmpleadoPermanente emp1 = new EmpleadoPermanente(
                1, "Juan", "Pérez", "12345678",
                LocalDate.of(2020, 1, 1), 50000, 5000
        );
        EmpleadoTemporal emp2 = new EmpleadoTemporal(
                2, "María", "García", "87654321",
                LocalDate.of(2023, 6, 1), 45000,
                LocalDate.of(2024, 6, 1), 3000
        );

        rrhh.agregarEmpleado(emp1);
        ti.agregarEmpleado(emp2);
        empleados.addAll(emp1, emp2);
    }

    private void configurarFiltrosYSeleccion() {
        empleadosFiltrados = new FilteredList<>(empleados, e -> true);
        listaEmpleados.setItems(empleadosFiltrados);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        comboFiltroDepartamento.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());

        listaEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldEmp, nuevoEmp) -> {
            if (nuevoEmp != null) {
                cargarEmpleadoEnFormulario(nuevoEmp);
            }
        });
    }

    private void aplicarFiltros() {
        String texto = (txtBuscar.getText() == null) ? "" : txtBuscar.getText().trim().toLowerCase();
        Departamento filtroDepto = comboFiltroDepartamento.getValue();

        empleadosFiltrados.setPredicate(emp -> {
            boolean coincideTexto = texto.isEmpty()
                    || emp.getNombre().toLowerCase().contains(texto)
                    || emp.getApellido().toLowerCase().contains(texto)
                    || emp.getCc().toLowerCase().contains(texto);

            boolean coincideDepto = (filtroDepto == null || filtroDepto.getId() == -1)
                    || (emp.getDepartamento() != null && emp.getDepartamento().getId() == filtroDepto.getId());

            return coincideTexto && coincideDepto;
        });
    }

    private void actualizarCombosDepartamentos() {
        Departamento seleccionForm = comboDepartamentos.getValue();
        comboDepartamentos.setItems(departamentos);
        if (seleccionForm != null && departamentos.contains(seleccionForm)) {
            comboDepartamentos.setValue(seleccionForm);
        } else if (!departamentos.isEmpty()) {
            comboDepartamentos.setValue(departamentos.get(0));
        }

        Departamento seleccionFiltro = comboFiltroDepartamento.getValue();
        ObservableList<Departamento> opcionesFiltro = FXCollections.observableArrayList();
        opcionesFiltro.add(OPCION_TODOS);
        opcionesFiltro.addAll(departamentos);
        comboFiltroDepartamento.setItems(opcionesFiltro);
        if (seleccionFiltro != null && opcionesFiltro.contains(seleccionFiltro)) {
            comboFiltroDepartamento.setValue(seleccionFiltro);
        } else {
            comboFiltroDepartamento.setValue(OPCION_TODOS);
        }
    }

    private void cargarEmpleadoEnFormulario(Empleado emp) {
        txtNombre.setText(emp.getNombre());
        txtApellido.setText(emp.getApellido());
        txtCc.setText(emp.getCc());
        fechaContratacion.setValue(emp.getFechaContratacion());
        txtSalarioBase.setText(String.valueOf(emp.getSalarioBase()));
        comboDepartamentos.setValue(emp.getDepartamento());

        if (emp instanceof EmpleadoPermanente ep) {
            rbPermanente.setSelected(true);
            txtBonificacionAntiguedad.setText(String.valueOf(ep.getBonificacionAntiguedad()));
            txtBonificacionProyecto.clear();
            fechaFinContrato.setValue(null);
        } else if (emp instanceof EmpleadoTemporal et) {
            rbTemporal.setSelected(true);
            fechaFinContrato.setValue(et.getFechaFinContrato());
            txtBonificacionProyecto.setText(String.valueOf(et.getBonificacionProyecto()));
            txtBonificacionAntiguedad.clear();
        }
    }

    @FXML
    private void agregarEmpleado() {
        try {
            if (!validarCamposObligatorios()) return;

            int nuevoId = empleados.stream().mapToInt(Empleado::getId).max().orElse(0) + 1;
            Empleado nuevoEmpleado = construirEmpleadoDesdeFormulario(nuevoId);
            if (nuevoEmpleado == null) return;

            Departamento departamento = comboDepartamentos.getValue();
            departamento.agregarEmpleado(nuevoEmpleado);
            empleados.add(nuevoEmpleado);

            guardarCambios();
            limpiarFormulario();
            mostrarInfo("Empleado agregado correctamente.");
        } catch (NumberFormatException e) {
            mostrarError("Ingrese valores numéricos válidos para el salario y la bonificación.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void actualizarEmpleado() {
        Empleado seleccionado = listaEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccione un empleado de la lista para actualizar.");
            return;
        }

        try {
            if (!validarCamposObligatorios()) return;

            Empleado actualizado = construirEmpleadoDesdeFormulario(seleccionado.getId());
            if (actualizado == null) return;

            // Desvincular del departamento anterior y asignar al nuevo
            if (seleccionado.getDepartamento() != null) {
                seleccionado.getDepartamento().removerEmpleado(seleccionado);
            }
            Departamento nuevoDepto = comboDepartamentos.getValue();
            nuevoDepto.agregarEmpleado(actualizado);

            int indice = empleados.indexOf(seleccionado);
            if (indice >= 0) {
                empleados.set(indice, actualizado);
            }

            guardarCambios();
            listaEmpleados.refresh();
            mostrarInfo("Datos del empleado actualizados correctamente.");
        } catch (NumberFormatException e) {
            mostrarError("Ingrese valores numéricos válidos para el salario y la bonificación.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarEmpleado() {
        Empleado seleccionado = listaEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccione un empleado de la lista para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Desea eliminar a " + seleccionado.getNombre() + " " + seleccionado.getApellido() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (seleccionado.getDepartamento() != null) {
                seleccionado.getDepartamento().removerEmpleado(seleccionado);
            }
            empleados.remove(seleccionado);
            guardarCambios();
            limpiarFormulario();
            areaReporte.clear();
        }
    }

    @FXML
    private void crearDepartamento() {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nuevo Departamento");
        dialogo.setHeaderText("Crear un nuevo departamento");
        dialogo.setContentText("Nombre del departamento:");

        Optional<String> resultado = dialogo.showAndWait();
        resultado.ifPresent(nombre -> {
            String nombreLimpio = nombre.trim();
            if (nombreLimpio.isEmpty()) {
                mostrarError("El nombre del departamento no puede estar vacío.");
                return;
            }
            boolean existe = departamentos.stream()
                    .anyMatch(d -> d.getNombre().equalsIgnoreCase(nombreLimpio));
            if (existe) {
                mostrarError("Ya existe un departamento con ese nombre.");
                return;
            }

            int nuevoId = departamentos.stream().mapToInt(Departamento::getId).max().orElse(0) + 1;
            Departamento nuevo = new Departamento(nuevoId, nombreLimpio);
            departamentos.add(nuevo);
            actualizarCombosDepartamentos();
            comboDepartamentos.setValue(nuevo);
            guardarCambios();
        });
    }

    @FXML
    private void generarReporte() {
        Empleado empleadoSeleccionado = listaEmpleados.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            areaReporte.setText(empleadoSeleccionado.generarReporteDesempeno());
        } else if (comboFiltroDepartamento.getValue() != null && comboFiltroDepartamento.getValue().getId() != -1) {
            areaReporte.setText(comboFiltroDepartamento.getValue().generarReporteDepartamento());
        } else if (comboDepartamentos.getValue() != null) {
            areaReporte.setText(comboDepartamentos.getValue().generarReporteDepartamento());
        } else {
            mostrarError("Seleccione un empleado o un departamento para generar el reporte.");
        }
    }

    @FXML
    private void exportarReporte() {
        String contenido = areaReporte.getText();
        if (contenido == null || contenido.isBlank()) {
            mostrarError("Primero genere un reporte antes de exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte");
        fileChooser.setInitialFileName("reporte_compuwork.txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo de texto (*.txt)", "*.txt")
        );

        File archivo = fileChooser.showSaveDialog(areaReporte.getScene().getWindow());
        if (archivo != null) {
            try {
                Files.writeString(archivo.toPath(), contenido, StandardCharsets.UTF_8);
                mostrarInfo("Reporte exportado exitosamente en:\n" + archivo.getAbsolutePath());
            } catch (IOException e) {
                mostrarError("No se pudo guardar el reporte: " + e.getMessage());
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtCc.clear();
        fechaContratacion.setValue(null);
        txtSalarioBase.clear();
        txtBonificacionAntiguedad.clear();
        txtBonificacionProyecto.clear();
        fechaFinContrato.setValue(null);
        listaEmpleados.getSelectionModel().clearSelection();
    }

    private boolean validarCamposObligatorios() {
        if (txtNombre.getText().trim().isEmpty()
                || txtApellido.getText().trim().isEmpty()
                || txtCc.getText().trim().isEmpty()
                || fechaContratacion.getValue() == null
                || txtSalarioBase.getText().trim().isEmpty()
                || comboDepartamentos.getValue() == null) {
            mostrarError("Por favor, complete todos los campos obligatorios.");
            return false;
        }
        return true;
    }

    private Empleado construirEmpleadoDesdeFormulario(int id) {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String cc = txtCc.getText().trim();
        LocalDate fechaContr = fechaContratacion.getValue();
        double salarioBase = Double.parseDouble(txtSalarioBase.getText().trim());

        if (rbPermanente.isSelected()) {
            double bonificacion = Double.parseDouble(txtBonificacionAntiguedad.getText().trim());
            return new EmpleadoPermanente(id, nombre, apellido, cc, fechaContr, salarioBase, bonificacion);
        } else {
            LocalDate fechaFin = fechaFinContrato.getValue();
            if (fechaFin == null) {
                mostrarError("Por favor, seleccione la fecha de fin de contrato.");
                return null;
            }
            double bonificacion = Double.parseDouble(txtBonificacionProyecto.getText().trim());
            return new EmpleadoTemporal(id, nombre, apellido, cc, fechaContr, salarioBase, fechaFin, bonificacion);
        }
    }

    private void guardarCambios() {
        try {
            persistencia.guardar(departamentos, empleados);
        } catch (IOException e) {
            mostrarError("Error al guardar los datos en disco: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

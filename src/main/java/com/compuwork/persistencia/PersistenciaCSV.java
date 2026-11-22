package com.compuwork.persistencia;

import com.compuwork.modelo.Departamento;
import com.compuwork.modelo.Empleado;
import com.compuwork.modelo.EmpleadoPermanente;
import com.compuwork.modelo.EmpleadoTemporal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Gestiona la persistencia local de departamentos y empleados en archivos CSV
 * separados por punto y coma (;).
 */
public class PersistenciaCSV {

    private static final String ARCHIVO_DEPARTAMENTOS = "departamentos.csv";
    private static final String ARCHIVO_EMPLEADOS = "empleados.csv";
    private static final String SEPARADOR = ";";

    private final Path directorioDatos;

    public PersistenciaCSV(Path directorioDatos) {
        this.directorioDatos = directorioDatos;
    }

    public boolean existenDatosGuardados() {
        return Files.exists(directorioDatos.resolve(ARCHIVO_DEPARTAMENTOS));
    }

    public void guardar(List<Departamento> departamentos, List<Empleado> empleados) throws IOException {
        Files.createDirectories(directorioDatos);

        Path rutaDeptos = directorioDatos.resolve(ARCHIVO_DEPARTAMENTOS);
        try (BufferedWriter writer = Files.newBufferedWriter(rutaDeptos, StandardCharsets.UTF_8)) {
            writer.write("id;nombre");
            writer.newLine();
            for (Departamento d : departamentos) {
                writer.write(d.getId() + SEPARADOR + limpiarTexto(d.getNombre()));
                writer.newLine();
            }
        }

        Path rutaEmps = directorioDatos.resolve(ARCHIVO_EMPLEADOS);
        try (BufferedWriter writer = Files.newBufferedWriter(rutaEmps, StandardCharsets.UTF_8)) {
            writer.write("tipo;id;nombre;apellido;cc;fechaContratacion;salarioBase;idDepartamento;bonificacion;fechaFinContrato");
            writer.newLine();
            for (Empleado e : empleados) {
                int idDepto = (e.getDepartamento() != null) ? e.getDepartamento().getId() : -1;
                if (e instanceof EmpleadoPermanente ep) {
                    writer.write(String.format(Locale.US, "PERMANENTE;%d;%s;%s;%s;%s;%.2f;%d;%.2f;",
                            ep.getId(),
                            limpiarTexto(ep.getNombre()),
                            limpiarTexto(ep.getApellido()),
                            limpiarTexto(ep.getCc()),
                            ep.getFechaContratacion(),
                            ep.getSalarioBase(),
                            idDepto,
                            ep.getBonificacionAntiguedad()));
                } else if (e instanceof EmpleadoTemporal et) {
                    String fechaFin = (et.getFechaFinContrato() != null) ? et.getFechaFinContrato().toString() : "";
                    writer.write(String.format(Locale.US, "TEMPORAL;%d;%s;%s;%s;%s;%.2f;%d;%.2f;%s",
                            et.getId(),
                            limpiarTexto(et.getNombre()),
                            limpiarTexto(et.getApellido()),
                            limpiarTexto(et.getCc()),
                            et.getFechaContratacion(),
                            et.getSalarioBase(),
                            idDepto,
                            et.getBonificacionProyecto(),
                            fechaFin));
                }
                writer.newLine();
            }
        }
    }

    public List<Departamento> cargarDepartamentos() throws IOException {
        List<Departamento> departamentos = new ArrayList<>();
        Path rutaDeptos = directorioDatos.resolve(ARCHIVO_DEPARTAMENTOS);
        if (!Files.exists(rutaDeptos)) {
            return departamentos;
        }

        try (BufferedReader reader = Files.newBufferedReader(rutaDeptos, StandardCharsets.UTF_8)) {
            String linea = reader.readLine(); // saltar encabezado
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] partes = linea.split(SEPARADOR, -1);
                if (partes.length >= 2) {
                    int id = Integer.parseInt(partes[0].trim());
                    String nombre = partes[1].trim();
                    departamentos.add(new Departamento(id, nombre));
                }
            }
        }
        return departamentos;
    }

    public List<Empleado> cargarEmpleados(List<Departamento> departamentos) throws IOException {
        List<Empleado> empleados = new ArrayList<>();
        Path rutaEmps = directorioDatos.resolve(ARCHIVO_EMPLEADOS);
        if (!Files.exists(rutaEmps)) {
            return empleados;
        }

        Map<Integer, Departamento> mapaDeptos = new HashMap<>();
        for (Departamento d : departamentos) {
            mapaDeptos.put(d.getId(), d);
        }

        try (BufferedReader reader = Files.newBufferedReader(rutaEmps, StandardCharsets.UTF_8)) {
            String linea = reader.readLine(); // saltar encabezado
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] partes = linea.split(SEPARADOR, -1);
                if (partes.length < 9) continue;

                String tipo = partes[0].trim();
                int id = Integer.parseInt(partes[1].trim());
                String nombre = partes[2].trim();
                String apellido = partes[3].trim();
                String cc = partes[4].trim();
                LocalDate fechaContr = LocalDate.parse(partes[5].trim());
                double salarioBase = Double.parseDouble(partes[6].trim());
                int idDepto = Integer.parseInt(partes[7].trim());
                double bonificacion = Double.parseDouble(partes[8].trim());

                Empleado emp;
                if ("PERMANENTE".equalsIgnoreCase(tipo)) {
                    emp = new EmpleadoPermanente(id, nombre, apellido, cc, fechaContr, salarioBase, bonificacion);
                } else {
                    LocalDate fechaFin = (partes.length > 9 && !partes[9].isBlank())
                            ? LocalDate.parse(partes[9].trim())
                            : null;
                    emp = new EmpleadoTemporal(id, nombre, apellido, cc, fechaContr, salarioBase, fechaFin, bonificacion);
                }

                Departamento depto = mapaDeptos.get(idDepto);
                if (depto != null) {
                    depto.agregarEmpleado(emp);
                }
                empleados.add(emp);
            }
        }
        return empleados;
    }

    private String limpiarTexto(String texto) {
        return (texto == null) ? "" : texto.replace(SEPARADOR, ",").replace("\n", " ").trim();
    }
}

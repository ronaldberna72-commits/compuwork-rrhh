package com.compuwork.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un departamento organizacional dentro de CompuWork.
 */
public class Departamento {

    private int id;
    private String nombre;
    private final List<Empleado> empleados;

    public Departamento(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.empleados = new ArrayList<>();
    }

    public void agregarEmpleado(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("No se puede agregar un empleado nulo al departamento.");
        }
        if (!empleados.contains(empleado)) {
            empleados.add(empleado);
            empleado.setDepartamento(this);
        }
    }

    public void removerEmpleado(Empleado empleado) {
        if (empleado != null && empleados.remove(empleado)) {
            empleado.setDepartamento(null);
        }
    }

    public String generarReporteDepartamento() {
        StringBuilder reporte = new StringBuilder();
        reporte.append(String.format("Reporte del Departamento: %s (ID: %d)%n", nombre, id));
        reporte.append(String.format("Número total de empleados: %d%n%n", empleados.size()));

        double totalSalarios = 0;
        reporte.append(String.format("Lista de empleados:%n"));

        for (Empleado empleado : empleados) {
            double salario = empleado.calcularSalarioTotal();
            totalSalarios += salario;
            reporte.append(String.format("- %s %s: %.2f%n",
                    empleado.getNombre(), empleado.getApellido(), salario));
        }

        reporte.append(String.format("%nTotal de salarios: %.2f", totalSalarios));
        return reporte.toString();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Empleado> getEmpleados() { return new ArrayList<>(empleados); }

    @Override
    public String toString() {
        return String.format("%s (ID: %d)", nombre, id);
    }
}

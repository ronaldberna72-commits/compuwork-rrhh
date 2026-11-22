package com.compuwork.modelo;

import java.time.LocalDate;

/**
 * Clase abstracta base que representa a un empleado dentro del sistema CompuWork.
 * Aplica encapsulamiento y define el contrato polimórfico para el cálculo salarial.
 */
public abstract class Empleado {

    private int id;
    private String nombre;
    private String apellido;
    private String cc;
    private LocalDate fechaContratacion;
    private double salarioBase;
    private Departamento departamento;

    public Empleado(int id, String nombre, String apellido, String cc,
                    LocalDate fechaContratacion, double salarioBase) {
        if (salarioBase < 0) {
            throw new IllegalArgumentException("El salario base no puede ser negativo.");
        }
        if (fechaContratacion == null) {
            throw new IllegalArgumentException("La fecha de contratación es obligatoria.");
        }
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cc = cc;
        this.fechaContratacion = fechaContratacion;
        this.salarioBase = salarioBase;
    }

    public abstract double calcularSalarioTotal();

    public String generarReporteDesempeno() {
        String nombreDepartamento = (departamento != null) ? departamento.getNombre() : "No asignado";
        return String.format(
                "Reporte de Desempeño - Empleado: %s %s%n" +
                "ID: %d%n" +
                "Documento (CC): %s%n" +
                "Departamento: %s%n" +
                "Fecha de Contratación: %s%n" +
                "Salario Base: %.2f%n" +
                "Salario Total: %.2f",
                nombre, apellido, id, cc,
                nombreDepartamento,
                fechaContratacion,
                salarioBase,
                calcularSalarioTotal()
        );
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCc() { return cc; }
    public void setCc(String cc) { this.cc = cc; }

    public LocalDate getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(LocalDate fechaContratacion) {
        if (fechaContratacion == null) {
            throw new IllegalArgumentException("La fecha de contratación es obligatoria.");
        }
        this.fechaContratacion = fechaContratacion;
    }

    public double getSalarioBase() { return salarioBase; }
    public void setSalarioBase(double salarioBase) {
        if (salarioBase < 0) {
            throw new IllegalArgumentException("El salario base no puede ser negativo.");
        }
        this.salarioBase = salarioBase;
    }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    @Override
    public String toString() {
        String depto = (departamento != null) ? departamento.getNombre() : "Sin depto";
        return String.format("%s %s (ID: %d | CC: %s | %s)", nombre, apellido, id, cc, depto);
    }
}

package com.compuwork.modelo;

import java.time.LocalDate;

/**
 * Representa a un empleado con contrato a término indefinido.
 */
public class EmpleadoPermanente extends Empleado {

    private double bonificacionAntiguedad;

    public EmpleadoPermanente(int id, String nombre, String apellido, String cc,
                              LocalDate fechaContratacion, double salarioBase,
                              double bonificacionAntiguedad) {
        super(id, nombre, apellido, cc, fechaContratacion, salarioBase);
        if (bonificacionAntiguedad < 0) {
            throw new IllegalArgumentException("La bonificación por antigüedad no puede ser negativa.");
        }
        this.bonificacionAntiguedad = bonificacionAntiguedad;
    }

    public int getAniosServicio() {
        int anios = LocalDate.now().getYear() - getFechaContratacion().getYear();
        return Math.max(0, anios);
    }

    @Override
    public double calcularSalarioTotal() {
        return getSalarioBase() + (bonificacionAntiguedad * getAniosServicio());
    }

    public double getBonificacionAntiguedad() { return bonificacionAntiguedad; }
    public void setBonificacionAntiguedad(double bonificacionAntiguedad) {
        if (bonificacionAntiguedad < 0) {
            throw new IllegalArgumentException("La bonificación por antigüedad no puede ser negativa.");
        }
        this.bonificacionAntiguedad = bonificacionAntiguedad;
    }
}

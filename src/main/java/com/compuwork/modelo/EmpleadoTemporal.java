package com.compuwork.modelo;

import java.time.LocalDate;

/**
 * Representa a un empleado contratado por tiempo definido o por proyecto.
 */
public class EmpleadoTemporal extends Empleado {

    private LocalDate fechaFinContrato;
    private double bonificacionProyecto;

    public EmpleadoTemporal(int id, String nombre, String apellido, String cc,
                            LocalDate fechaContratacion, double salarioBase,
                            LocalDate fechaFinContrato, double bonificacionProyecto) {
        super(id, nombre, apellido, cc, fechaContratacion, salarioBase);
        if (bonificacionProyecto < 0) {
            throw new IllegalArgumentException("La bonificación por proyecto no puede ser negativa.");
        }
        this.fechaFinContrato = fechaFinContrato;
        this.bonificacionProyecto = bonificacionProyecto;
    }

    @Override
    public double calcularSalarioTotal() {
        return getSalarioBase() + bonificacionProyecto;
    }

    public LocalDate getFechaFinContrato() { return fechaFinContrato; }
    public void setFechaFinContrato(LocalDate fechaFinContrato) {
        this.fechaFinContrato = fechaFinContrato;
    }

    public double getBonificacionProyecto() { return bonificacionProyecto; }
    public void setBonificacionProyecto(double bonificacionProyecto) {
        if (bonificacionProyecto < 0) {
            throw new IllegalArgumentException("La bonificación por proyecto no puede ser negativa.");
        }
        this.bonificacionProyecto = bonificacionProyecto;
    }
}

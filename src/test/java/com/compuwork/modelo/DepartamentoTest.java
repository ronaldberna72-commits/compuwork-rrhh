package com.compuwork.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DepartamentoTest {

    private Departamento departamento;
    private EmpleadoPermanente empleado1;
    private EmpleadoTemporal empleado2;

    @BeforeEach
    void setUp() {
        departamento = new Departamento(1, "Tecnología");
        empleado1 = new EmpleadoPermanente(
                1, "Juan", "Pérez", "12345678",
                LocalDate.of(2020, 1, 1), 50000, 5000
        );
        empleado2 = new EmpleadoTemporal(
                2, "María", "García", "87654321",
                LocalDate.of(2023, 6, 1), 45000,
                LocalDate.of(2024, 6, 1), 3000
        );
    }

    @Test
    void testAgregarEmpleado() {
        departamento.agregarEmpleado(empleado1);

        assertEquals(1, departamento.getEmpleados().size());
        assertTrue(departamento.getEmpleados().contains(empleado1));
        assertEquals(departamento, empleado1.getDepartamento());
    }

    @Test
    void testRemoverEmpleado() {
        departamento.agregarEmpleado(empleado1);
        departamento.removerEmpleado(empleado1);

        assertEquals(0, departamento.getEmpleados().size());
        assertNull(empleado1.getDepartamento());
    }

    @Test
    void testGenerarReporteDepartamento() {
        departamento.agregarEmpleado(empleado1);
        departamento.agregarEmpleado(empleado2);

        String reporte = departamento.generarReporteDepartamento();

        assertTrue(reporte.contains("Tecnología"));
        assertTrue(reporte.contains("2"));
        assertTrue(reporte.contains("Juan Pérez"));
        assertTrue(reporte.contains("María García"));
    }

    @Test
    void testCalculoTotalSalarios() {
        departamento.agregarEmpleado(empleado1);
        departamento.agregarEmpleado(empleado2);

        String reporte = departamento.generarReporteDepartamento();
        double salarioEsperado = empleado1.calcularSalarioTotal()
                + empleado2.calcularSalarioTotal();

        assertTrue(reporte.contains(String.format("%.2f", salarioEsperado)));
    }
}

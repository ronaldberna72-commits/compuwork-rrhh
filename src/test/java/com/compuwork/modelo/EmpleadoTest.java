package com.compuwork.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EmpleadoTest {

    private EmpleadoPermanente empleadoPermanente;
    private EmpleadoTemporal empleadoTemporal;
    private Departamento departamento;

    @BeforeEach
    void setUp() {
        empleadoPermanente = new EmpleadoPermanente(
                1, "Cristian", "Salcedo", "12345678",
                LocalDate.of(2020, 1, 1), 50000, 5000
        );

        empleadoTemporal = new EmpleadoTemporal(
                2, "María", "García", "87654321",
                LocalDate.of(2023, 6, 1), 45000,
                LocalDate.of(2024, 6, 1), 3000
        );

        departamento = new Departamento(1, "Recursos Humanos");
    }

    @Test
    void testCalcularSalarioEmpleadoPermanente() {
        int aniosEsperados = LocalDate.now().getYear() - 2020;
        double salarioEsperado = 50000 + (5000.0 * aniosEsperados);

        assertEquals(salarioEsperado, empleadoPermanente.calcularSalarioTotal(), 0.001);
    }

    @Test
    void testCalcularSalarioEmpleadoTemporal() {
        double salarioEsperado = 45000 + 3000;
        assertEquals(salarioEsperado, empleadoTemporal.calcularSalarioTotal(), 0.001);
    }

    @Test
    void testAsignacionDepartamento() {
        departamento.agregarEmpleado(empleadoPermanente);

        assertEquals(departamento, empleadoPermanente.getDepartamento());
        assertTrue(departamento.getEmpleados().contains(empleadoPermanente));
    }

    @Test
    void testRemoverEmpleadoDeDepartamento() {
        departamento.agregarEmpleado(empleadoPermanente);
        departamento.removerEmpleado(empleadoPermanente);

        assertNull(empleadoPermanente.getDepartamento());
        assertFalse(departamento.getEmpleados().contains(empleadoPermanente));
    }

    @Test
    void testGenerarReporteDesempeno() {
        departamento.agregarEmpleado(empleadoPermanente);
        String reporte = empleadoPermanente.generarReporteDesempeno();

        assertTrue(reporte.contains("Cristian Salcedo"));
        assertTrue(reporte.contains("Recursos Humanos"));
        assertTrue(reporte.contains("50000"));
    }
}

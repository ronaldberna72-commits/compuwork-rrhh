package com.compuwork.persistencia;

import com.compuwork.modelo.Departamento;
import com.compuwork.modelo.Empleado;
import com.compuwork.modelo.EmpleadoPermanente;
import com.compuwork.modelo.EmpleadoTemporal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenciaCSVTest {

    @Test
    void testGuardarYCargarDatos(@TempDir Path tempDir) throws IOException {
        PersistenciaCSV persistencia = new PersistenciaCSV(tempDir);

        Departamento rrhh = new Departamento(1, "Recursos Humanos");
        Departamento ti = new Departamento(2, "TI");

        EmpleadoPermanente emp1 = new EmpleadoPermanente(
                1, "Ana", "López", "11223344",
                LocalDate.of(2021, 3, 15), 60000, 4000
        );
        EmpleadoTemporal emp2 = new EmpleadoTemporal(
                2, "Carlos", "Ruiz", "55667788",
                LocalDate.of(2024, 1, 10), 40000,
                LocalDate.of(2024, 12, 31), 2500
        );

        rrhh.agregarEmpleado(emp1);
        ti.agregarEmpleado(emp2);

        persistencia.guardar(List.of(rrhh, ti), List.of(emp1, emp2));

        assertTrue(persistencia.existenDatosGuardados());

        List<Departamento> deptosCargados = persistencia.cargarDepartamentos();
        List<Empleado> empsCargados = persistencia.cargarEmpleados(deptosCargados);

        assertEquals(2, deptosCargados.size());
        assertEquals(2, empsCargados.size());
        assertEquals("Ana", empsCargados.get(0).getNombre());
        assertEquals("Recursos Humanos", empsCargados.get(0).getDepartamento().getNombre());
        assertInstanceOf(EmpleadoTemporal.class, empsCargados.get(1));
    }
}

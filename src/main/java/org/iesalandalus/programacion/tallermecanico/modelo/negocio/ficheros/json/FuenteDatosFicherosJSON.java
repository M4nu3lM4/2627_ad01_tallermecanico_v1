package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IClientes;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IFuenteDatos;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ITrabajos;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IVehiculos;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.xml.Clientes;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.xml.Trabajos;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.xml.Vehiculos;

public class FuenteDatosFicherosJSON implements IFuenteDatos {
    @Override
    public IClientes crearClientes() {
        return Clientes.getInstancia();
    }

    @Override
    public IVehiculos crearVehiculos() {
        return Vehiculos.getInstancia();
    }

    @Override
    public ITrabajos crearTrabajos() {
        return Trabajos.getInstancia();
    }
}

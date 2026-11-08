package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IVehiculos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vehiculos implements IVehiculos {

    private static final String FICHERO_VEHICULOS = String.format("%s%s%s%s%s%s%s", "datos", File.separator, "ficheros", File.separator, "json", File.separator, "vehiculos.json");
    private static Vehiculos instancia;

    private final ObjectMapper mapper;

    private Vehiculos(){mapper = new ObjectMapper();}

    static Vehiculos getInstancia() {
        if (instancia == null) {
            instancia = new Vehiculos();
        }
        return instancia;
    }

    @Override
    public void comenzar() {

    }

    @Override
    public void terminar() {

    }

    @Override
    public List<Vehiculo> get() {
        return leer();
    }

    private List<Vehiculo> leer(){
        List<Vehiculo> vehiculos = new ArrayList<>();
        File fichero = new File(FICHERO_VEHICULOS);
        if (fichero.exists()) {
            try {
                vehiculos = mapper.readValue(fichero, new TypeReference<List<Vehiculo>>() { });
            } catch (IOException e) {
                System.out.printf("Error al leer el fichero %s. --> %s%n", FICHERO_VEHICULOS, e.getMessage());
            }
        }
        return vehiculos;
    }

    private void escribir(List<Vehiculo> vehiculos){
        File fichero = new File(FICHERO_VEHICULOS);
        try {
            fichero.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(fichero, vehiculos);
        } catch (IOException e) {
            System.out.printf("Error al escribir el fichero %s. --> %s%n", FICHERO_VEHICULOS, e.getMessage());
        }
    }

    @Override
    public void insertar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(vehiculo, "No se puede insertar un vehículo nulo.");
        List<Vehiculo> vehiculos = leer();
        if (vehiculos.contains(vehiculo)) {
            throw new TallerMecanicoExcepcion("Ya existe un vehículo con esa matrícula.");
        }
        vehiculos.add(vehiculo);
        escribir(vehiculos);
    }

    @Override
    public Vehiculo buscar(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "No se puede buscar un vehículo nulo.");
        List<Vehiculo> vehiculos = leer();
        int indice = vehiculos.indexOf(vehiculo);
        return (indice == -1) ? null : vehiculos.get(indice);
    }

    @Override
    public void borrar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(vehiculo, "No se puede borrar un vehículo nulo.");
        List<Vehiculo> vehiculos = leer();
        if (!vehiculos.contains(vehiculo)) {
            throw new TallerMecanicoExcepcion("No existe ningún vehículo con esa matrícula.");
        }
        vehiculos.remove(vehiculo);
        escribir(vehiculos);

    }
}

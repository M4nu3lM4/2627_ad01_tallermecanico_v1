package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.*;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ITrabajos;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class Trabajos implements ITrabajos {
    private static final String FICHERO_TRABAJOS = String.format("%s%s%s%s%s%s%s", "datos", File.separator, "ficheros", File.separator, "xml", File.separator, "trabajos.json");
    private static Trabajos instancia;
    private static final TypeReference<List<Trabajo>> TYPE_LIST_TRABAJO = new TypeReference<>() { };
    private static final ObjectMapper mapper = new ObjectMapper();

    static {

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.findAndRegisterModules();
    }


    private Trabajos(){

    }

    static Trabajos getInstancia() {
        if (instancia == null) {
            instancia = new Trabajos();
        }
        return instancia;
    }

    private List<Trabajo> leer() {
        File fichero = new File(FICHERO_TRABAJOS);
        if (!fichero.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(fichero, TYPE_LIST_TRABAJO);
        } catch (IOException e) {
            throw new IllegalStateException("No se ha podido leer el fichero de trabajos.", e);
        }
    }

    private void escribir(List<Trabajo> trabajos){
        File fichero = new File(FICHERO_TRABAJOS);
        try {
            fichero.getParentFile().mkdirs();
            mapper.writerFor(TYPE_LIST_TRABAJO).withDefaultPrettyPrinter().writeValue(fichero, trabajos);
        } catch (IOException e) {
            throw new IllegalStateException("No se ha podido escribir en el fichero de trabajos.", e);
        }

    }

    @Override
    public void comenzar() {

    }

    @Override
    public void terminar() {

    }

    @Override
    public List<Trabajo> get() {
        return leer();
    }

    @Override
    public List<Trabajo> get(Cliente cliente) {
        List<Trabajo> trabajosCliente = new ArrayList<>();
        for (Trabajo trabajo : leer()) {
            if (trabajo.getCliente().equals(cliente)) {
                trabajosCliente.add(trabajo);
            }
        }
        return trabajosCliente;
    }

    @Override
    public List<Trabajo> get(Vehiculo vehiculo) {
        List<Trabajo> trabajosVehiculo = new ArrayList<>();
        for (Trabajo trabajo : leer()) {
            if (trabajo.getVehiculo().equals(vehiculo)) {
                trabajosVehiculo.add(trabajo);
            }
        }
        return trabajosVehiculo;
    }

    @Override
    public Map<TipoTrabajo, Integer> getEstadisticasMensuales(LocalDate mes) {
        Objects.requireNonNull(mes, "El mes no puede ser nulo.");
        Map<TipoTrabajo, Integer> estadisticas = inicializarEstadisticas();
        for (Trabajo trabajo : leer()) {
            LocalDate fechaInicio = trabajo.getFechaInicio();
            if (fechaInicio.getMonth().equals(mes.getMonth()) && fechaInicio.getYear() == mes.getYear()) {
                TipoTrabajo tipoTrabajo = TipoTrabajo.get(trabajo);
                estadisticas.put(tipoTrabajo, estadisticas.get(tipoTrabajo) + 1);
            }
        }
        return estadisticas;
    }

    private Map<TipoTrabajo, Integer> inicializarEstadisticas() {
        Map<TipoTrabajo, Integer> estadisticas = new EnumMap<>(TipoTrabajo.class);
        for (TipoTrabajo tipoTrabajo : TipoTrabajo.values()) {
            estadisticas.put(tipoTrabajo, 0);
        }
        return estadisticas;
    }

    @Override
    public void insertar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(trabajo, "No se puede insertar un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        for (Trabajo trabajoExistente : trabajos) {
            if (!trabajoExistente.estaCerrado()) {
                if (trabajoExistente.getCliente().equals(trabajo.getCliente())) {
                    throw new TallerMecanicoExcepcion("El cliente tiene otro trabajo en curso.");
                }
                if (trabajoExistente.getVehiculo().equals(trabajo.getVehiculo())) {
                    throw new TallerMecanicoExcepcion("El vehículo está actualmente en el taller.");
                }
            } else {
                if (trabajoExistente.getCliente().equals(trabajo.getCliente()) && !trabajo.getFechaInicio().isAfter(trabajoExistente.getFechaFin())) {
                    throw new TallerMecanicoExcepcion("El cliente tiene otro trabajo posterior.");
                }
                if (trabajoExistente.getVehiculo().equals(trabajo.getVehiculo()) && !trabajo.getFechaInicio().isAfter(trabajoExistente.getFechaFin())) {
                    throw new TallerMecanicoExcepcion("El vehículo tiene otro trabajo posterior.");
                }
            }
        }
        trabajos.add(trabajo);
        escribir(trabajos);
    }

    @Override
    public Trabajo anadirHoras(Trabajo trabajo, int horas) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(trabajo, "No puedo añadir horas a un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        Trabajo trabajoEncontrado = getTrabajoAbierto(trabajos, trabajo.getVehiculo());
        trabajoEncontrado.anadirHoras(horas);
        escribir(trabajos);
        return trabajoEncontrado;
    }

    private Trabajo getTrabajoAbierto(List<Trabajo> trabajos, Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        for (Trabajo trabajo : trabajos) {
            if (trabajo.getVehiculo().equals(vehiculo) && !trabajo.estaCerrado()) {
                return trabajo;
            }
        }
        throw new TallerMecanicoExcepcion("No existe ningún trabajo abierto para dicho vehículo.");
    }

    @Override
    public Trabajo anadirPrecioMaterial(Trabajo trabajo, float precioMaterial) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(trabajo, "No puedo añadir precio del material a un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        Trabajo trabajoEncontrado = getTrabajoAbierto(trabajos, trabajo.getVehiculo());
        if (trabajoEncontrado instanceof Mecanico mecanico) {
            mecanico.anadirPrecioMaterial(precioMaterial);
        } else {
            throw new TallerMecanicoExcepcion("No se puede añadir precio al material para este tipo de trabajos.");
        }
        escribir(trabajos);
        return trabajoEncontrado;
    }

    @Override
    public Trabajo cerrar(Trabajo trabajo, LocalDate fechaFin) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(trabajo, "No puedo cerrar un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        Trabajo trabajoEncontrado = getTrabajoAbierto(trabajos, trabajo.getVehiculo());
        trabajoEncontrado.cerrar(fechaFin);
        escribir(trabajos);
        return trabajoEncontrado;
    }

    @Override
    public Trabajo buscar(Trabajo trabajo) {
        Objects.requireNonNull(trabajo, "No se puede buscar un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        int indice = trabajos.indexOf(trabajo);
        return (indice == -1) ? null : trabajos.get(indice);
    }

    @Override
    public void borrar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(trabajo, "No se puede borrar un trabajo nulo.");
        List<Trabajo> trabajos = leer();
        if (!trabajos.contains(trabajo)) {
            throw new TallerMecanicoExcepcion("No existe ningún trabajo igual.");
        }
        trabajos.remove(trabajo);
        escribir(trabajos);
    }
}

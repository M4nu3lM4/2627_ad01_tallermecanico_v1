package org.iesalandalus.programacion.tallermecanico.modelo.dominio;

import com.fasterxml.jackson.annotation.*;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo"
)


@JsonSubTypes({
        @JsonSubTypes.Type(value = Mecanico.class, name = "Mecanico"),
        @JsonSubTypes.Type(value = Revision.class, name = "Revision")
})

public abstract class Trabajo {
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float FACTOR_DIA = 10F;
    @JsonProperty("cliente")
    private Cliente cliente;
    @JsonProperty("vehiculo")
    private Vehiculo vehiculo;
    @JsonProperty("fechaInicio")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    @JsonProperty("fechaFin")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    @JsonProperty("horas")
    private int horas;

    protected Trabajo(){}

    protected Trabajo(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio) {
        setCliente(cliente);
        setVehiculo(vehiculo);
        setFechaInicio(fechaInicio);
        horas = 0;
    }

    protected Trabajo(Trabajo trabajo) {
        Objects.requireNonNull(trabajo, "El trabajo no puede ser nulo.");
        cliente = new Cliente(trabajo.cliente);
        vehiculo = trabajo.vehiculo;
        fechaInicio = trabajo.fechaInicio;
        fechaFin = trabajo.fechaFin;
        horas = trabajo.horas;
    }
    @JsonIgnore
    public static Trabajo copiar(Trabajo trabajo) {
        Objects.requireNonNull(trabajo, "El trabajo no puede ser nulo.");
        Trabajo trabajoCopiado = null;
        if (trabajo instanceof Revision revision) {
            trabajoCopiado = new Revision(revision);
        } else if (trabajo instanceof Mecanico mecanico) {
            trabajoCopiado = new Mecanico(mecanico);
        }
        return trabajoCopiado;
    }
    @JsonIgnore
    public static Trabajo get(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "El vehículo no puede ser nulo.");
        return new Revision(Cliente.get("11111111H"), vehiculo, LocalDate.now());
    }
    @JsonProperty("cliente")
    public Cliente getCliente() {
        return cliente;
    }
    @JsonProperty("cliente")
    private void setCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo.");
        this.cliente = cliente;
    }
    @JsonProperty("vehiculo")
    public Vehiculo getVehiculo() {
        return vehiculo;
    }
    @JsonProperty("vehiculo")
    private void setVehiculo(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "El vehículo no puede ser nulo.");
        this.vehiculo = vehiculo;
    }
    @JsonProperty("fechaInicio")
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    @JsonProperty("fechaInicio")
    private void setFechaInicio(LocalDate fechaInicio) {
        Objects.requireNonNull(fechaInicio, "La fecha de inicio no puede ser nula.");
        if (fechaInicio.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser futura.");
        }
        this.fechaInicio = fechaInicio;
    }
    @JsonProperty("fechaFin")
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    @JsonProperty("fechaFin")
    private void setFechaFin(LocalDate fechaFin) {
        Objects.requireNonNull(fechaFin, "La fecha de fin no puede ser nula.");
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        if (fechaFin.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser futura.");
        }
        this.fechaFin = fechaFin;
    }
    @JsonProperty("horas")
    public int getHoras() {
        return horas;
    }

    public void anadirHoras(int horas) throws TallerMecanicoExcepcion {
        if (horas <= 0) {
            throw new IllegalArgumentException("Las horas a añadir deben ser mayores que cero.");
        }
        if (estaCerrado()) {
            throw new TallerMecanicoExcepcion("No se puede añadir horas, ya que el trabajo está cerrado.");
        }
        this.horas += horas;
    }
@JsonIgnore
    public boolean estaCerrado() {
        return fechaFin != null;
    }

    public void cerrar(LocalDate fechaFin) throws TallerMecanicoExcepcion {
        if (estaCerrado()) {
            throw new TallerMecanicoExcepcion("El trabajo ya está cerrado.");
        }
        setFechaFin(fechaFin);
    }

@JsonIgnore
    public float getPrecio() {
        return getPrecioFijo() + getPrecioEspecifico();
    }
@JsonIgnore
    private float getPrecioFijo() {
        return (estaCerrado()) ? FACTOR_DIA * getDias() : 0;
    }
@JsonIgnore
    private float getDias() {
        return (estaCerrado()) ? (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin) : 0;
    }
@JsonIgnore
    public abstract float getPrecioEspecifico();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trabajo trabajo)) return false;
        return Objects.equals(cliente, trabajo.cliente) && Objects.equals(vehiculo, trabajo.vehiculo) && Objects.equals(fechaInicio, trabajo.fechaInicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cliente, vehiculo, fechaInicio);
    }
}

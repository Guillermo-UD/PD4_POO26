package filey.controlador;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import filey.modelo.datos.Editorial;
import filey.modelo.datos.Reserva;
import filey.modelo.datos.Stand;
import filey.modelo.negocio.SistemaFiley;
import filey.modelo.persistencia.RepositorioFiley;

public final class ControladorFiley {
    private final SistemaFiley sistema;
    private final RepositorioFiley repositorio;

    public ControladorFiley(SistemaFiley sistema, RepositorioFiley repositorio) {
        this.sistema = sistema;
        this.repositorio = repositorio;
    }

    public String registrarStand(Stand stand) {
        try {
            sistema.registrarStand(stand);
            return "Stand " + stand.getId() + " registrado.";
        } catch (IllegalArgumentException e) {
            return "No se pudo registrar el stand: " + e.getMessage();
        }
    }

    public String registrarEditorial(Editorial editorial) {
        try {
            sistema.registrarEditorial(editorial);
            return "Editorial " + editorial.getNombre() + " registrada.";
        } catch (IllegalArgumentException e) {
            return "No se pudo registrar la editorial: " + e.getMessage();
        }
    }

    public String crearReserva(int idEditorial, int idStand, LocalDate fecha) {
        try {
            Reserva r = sistema.crearReserva(idEditorial, idStand, fecha);
            return "Reserva " + r.getId() + " creada. Total: $" + r.getPrecioTotal()
                    + ", vence el " + r.getVencimiento() + ".";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "No se pudo crear la reserva: " + e.getMessage();
        }
    }

    public String registrarPago(int idReserva, BigDecimal importe, LocalDate fecha) {
        try {
            sistema.registrarPago(idReserva, importe, fecha);
            return "Pago de $" + importe + " registrado.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "No se pudo registrar el pago: " + e.getMessage();
        }
    }

    public String bloquearStand(int idStand) {
        try {
            sistema.bloquearStand(idStand);
            return "Stand " + idStand + " bloqueado.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "No se pudo bloquear el stand: " + e.getMessage();
        }
    }

    public String desbloquearStand(int idStand) {
        try {
            sistema.desbloquearStand(idStand);
            return "Stand " + idStand + " desbloqueado.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "No se pudo desbloquear el stand: " + e.getMessage();
        }
    }

    public void comprobarFechas(LocalDate fecha) {
        sistema.comprobarFechas(fecha);
    }

    public List<Stand> consultarDisponibles() {
        return sistema.consultarDisponibles();
    }

    public List<Reserva> consultarReservas() {
        return sistema.consultarReservas();
    }
   
 public String guardarEnBaseDatos() {
    try {
        repositorio.guardar(sistema.consultarEditoriales(),
                sistema.consultarStands(), sistema.consultarReservas());
        return "Datos guardados en la base de datos.";
    } catch (SQLException | IllegalStateException e) {
        return "No se pudo guardar en la base de datos: " + e.getMessage();
    }
}
}
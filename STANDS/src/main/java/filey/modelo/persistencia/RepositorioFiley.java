package filey.modelo.persistencia;

import filey.modelo.datos.Editorial;
import filey.modelo.datos.Pago;
import filey.modelo.datos.Reserva;
import filey.modelo.datos.Stand;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public final class RepositorioFiley {

    public void guardar(List<Editorial> editoriales, List<Stand> stands, List<Reserva> reservas)
            throws SQLException {
        try (Connection con = ConexionBD.obtenerEscritura()) {
            con.setAutoCommit(false);
            try {
                guardarEditoriales(con, editoriales);
                guardarStands(con, stands);
                guardarReservas(con, reservas);
                guardarPagos(con, reservas);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private void guardarEditoriales(Connection con, List<Editorial> editoriales) throws SQLException {
        String sql = "INSERT INTO editorial (id, nombre) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE nombre = VALUES(nombre)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Editorial e : editoriales) {
                ps.setInt(1, e.getId());
                ps.setString(2, e.getNombre());
                ps.executeUpdate();
            }
        }
    }

    private void guardarStands(Connection con, List<Stand> stands) throws SQLException {
        String sql = "INSERT INTO stand (id, ancho, largo, precio_m2, estado) VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE ancho = VALUES(ancho), largo = VALUES(largo), "
                + "precio_m2 = VALUES(precio_m2), estado = VALUES(estado)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Stand s : stands) {
                ps.setInt(1, s.getId());
                ps.setBigDecimal(2, s.getAncho());
                ps.setBigDecimal(3, s.getLargo());
                ps.setBigDecimal(4, s.getPrecioPorMetroCuadrado());
                ps.setString(5, s.getEstado().name());
                ps.executeUpdate();
            }
        }
    }

    private void guardarReservas(Connection con, List<Reserva> reservas) throws SQLException {
        String sql = "INSERT INTO reserva (id, id_editorial, id_stand, fecha_creacion, vencimiento, "
                + "limite_liquidacion, vencida) VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE id_editorial = VALUES(id_editorial), "
                + "id_stand = VALUES(id_stand), fecha_creacion = VALUES(fecha_creacion), "
                + "vencimiento = VALUES(vencimiento), limite_liquidacion = VALUES(limite_liquidacion), "
                + "vencida = VALUES(vencida)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Reserva r : reservas) {
                ps.setInt(1, r.getId());
                ps.setInt(2, r.getEditorial().getId());
                ps.setInt(3, r.getStand().getId());
                ps.setObject(4, r.getFechaCreacion());
                ps.setObject(5, r.getVencimiento());
                ps.setObject(6, r.getLimiteLiquidacion());
                ps.setBoolean(7, r.isVencida());
                ps.executeUpdate();
            }
        }
    }

    private void guardarPagos(Connection con, List<Reserva> reservas) throws SQLException {
        String sql = "INSERT IGNORE INTO pago (id_reserva, importe, fecha) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Reserva r : reservas) {
                for (Pago p : r.getPagos()) {
                    ps.setInt(1, r.getId());
                    ps.setBigDecimal(2, p.getImporte());
                    ps.setObject(3, p.getFecha());
                    ps.executeUpdate();
                }
            }
        }
    }
}
package filey;

import java.math.BigDecimal;
import java.time.LocalDate;

import filey.controlador.ControladorFiley;
import filey.modelo.datos.Configuracion;
import filey.modelo.datos.Editorial;
import filey.modelo.datos.Reserva;
import filey.modelo.datos.Stand;
import filey.modelo.negocio.SistemaFiley;
import filey.modelo.notificaciones.Notificador;
import filey.modelo.persistencia.RepositorioFiley;;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        // Fechas de ejemplo relativas a hoy; se pueden cambiar sin modificar los servicios.
        LocalDate hoy = LocalDate.now();
        Configuracion configuracion = new Configuracion(hoy.plusDays(4), hoy.plusDays(10),
                hoy.plusDays(6), 3, 2);
        SistemaFiley sistema = new SistemaFiley(configuracion, new Notificador());
        ControladorFiley controlador = new ControladorFiley(sistema, new RepositorioFiley());

        titulo("CONFIGURACIÓN INICIAL");
        System.out.println("Hoy:                     " + hoy);
        System.out.println("Límite de descuento:     " + configuracion.getLimiteDescuento());
        System.out.println("Límite de reserva:       " + configuracion.getLimiteReserva());
        System.out.println("Límite de liquidación:   " + configuracion.getLimiteLiquidacion());
        System.out.println("Días para pagar reserva: " + configuracion.getDiasParaPagarReserva());
        System.out.println("Días de aviso previo:    " + configuracion.getDiasAnticipacionAviso());

        titulo("REGISTRO DE STANDS Y EDITORIALES");
        Stand primero = new Stand(1, new BigDecimal("5"), new BigDecimal("5"), new BigDecimal("200"));
        Stand segundo = new Stand(2, new BigDecimal("10"), new BigDecimal("5"), new BigDecimal("500"));
        Stand tercero = new Stand(3, new BigDecimal("4"), new BigDecimal("3"), new BigDecimal("100"));
        System.out.println(controlador.registrarStand(primero));
        System.out.println(controlador.registrarStand(segundo));
        System.out.println(controlador.registrarStand(tercero));
        System.out.println(controlador.registrarEditorial(new Editorial(1, "UADY Fmat")));
        System.out.println(controlador.registrarEditorial(new Editorial(2, "ADO")));
        System.out.println(controlador.registrarEditorial(new Editorial(3, "La gotita de Miel")));
        System.out.println("Stand 1: " + primero.getSuperficie() + " m2, total $" + primero.getPrecioTotal());
        System.out.println("Stand 2: " + segundo.getSuperficie() + " m2, total $" + segundo.getPrecioTotal());
        System.out.println("Stand 3: " + tercero.getSuperficie() + " m2, total $" + tercero.getPrecioTotal());
        System.out.println("Disponibles: "
                + controlador.consultarDisponibles().stream().map(Stand::getId).toList());

        titulo("DÍA 0: RESERVA Y PRIMER PAGO");
        System.out.println(controlador.crearReserva(1, 1, hoy));
        Reserva reserva = controlador.consultarReservas().get(0);
        System.out.println(controlador.registrarPago(reserva.getId(), new BigDecimal("2000"), hoy));
        mostrar(reserva);

        titulo("DÍA 2: AVISOS Y SEGUNDO PAGO");
        controlador.comprobarFechas(hoy.plusDays(2));
        System.out.println(controlador.registrarPago(reserva.getId(), new BigDecimal("2000"), hoy.plusDays(2)));
        mostrar(reserva);

        titulo("BLOQUEO Y DESBLOQUEO DEL STAND 2");
        System.out.println(controlador.bloquearStand(2));
        System.out.println("Stand 2: " + segundo.getEstado());
        System.out.println(controlador.desbloquearStand(2));
        System.out.println("Stand 2: " + segundo.getEstado());

        titulo("RESERVA SIN PAGO (STAND 2)");
        System.out.println(controlador.crearReserva(2, 2, hoy.plusDays(2)));
        Reserva sinPago = controlador.consultarReservas().get(1);
        controlador.comprobarFechas(hoy.plusDays(4));
        controlador.comprobarFechas(hoy.plusDays(6));
        System.out.println("Reserva " + sinPago.getId() + " vencida: " + (sinPago.isVencida() ? "sí" : "no")
                + " | Stand 2: " + segundo.getEstado());

        titulo("DÍA 8: AVISO DE LIQUIDACIÓN (sin duplicados)");
        controlador.comprobarFechas(hoy.plusDays(8));
        System.out.println("(segunda consulta del mismo día, no debe repetir avisos)");
        controlador.comprobarFechas(hoy.plusDays(8));

        titulo("DÍA 10: LIQUIDACIÓN FINAL");
        System.out.println(controlador.registrarPago(reserva.getId(), new BigDecimal("1000"), hoy.plusDays(10)));
        mostrar(reserva);

        titulo("GUARDADO EN BASE DE DATOS");
        System.out.println(controlador.guardarEnBaseDatos());
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    private static void mostrar(Reserva reserva) {
        System.out.println("  Stand " + reserva.getStand().getId() + ": " + reserva.getStand().getEstado()
                + " | abonado: $" + reserva.getAbonado() + " | pendiente: $" + reserva.getPendiente()
                + " | pagado: " + reserva.getPorcentajePagado() + "%");
    }
}
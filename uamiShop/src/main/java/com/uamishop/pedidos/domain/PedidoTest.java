package com.uamishop.pedidos.domain;

import com.uamishop.identidad.domain.Rol;
import com.uamishop.identidad.domain.Usuario;

import com.uamishop.shared.domain.Money;

import java.util.List;

public class PedidoTest {

    public static void main(String[] args) {

        System.out.println("PRUEBAS DEL AGGREGATE PEDIDO");

        crearPedidoValido();
        noPermitirPedidoSinDetalles();
        confirmarPedido();
        noConfirmarPedidoYaConfirmado();
        cancelarPedidoPendiente();
        noCancelarPedidoConPago();
        flujoCompletoPedido();

        System.out.println("FIN DE PRUEBAS DEL AGGREGATE PEDIDO");
    }

  
    static void crearPedidoValido() {
        try {
            Pedido pedido = new Pedido(crearUsuario(), List.of(crearDetalle()));
            System.out.println("✔ Crear pedido válido");
        } catch (Exception e) {
            System.out.println("✘ Falló crear pedido válido: " + e.getMessage());
        }
    }

   
    static void noPermitirPedidoSinDetalles() {
        try {
            new Pedido(crearUsuario(), List.of());
            System.out.println("✘ ERROR: Se permitió pedido sin detalles");
        } catch (Exception e) {
            System.out.println("✔ No se permite pedido sin detalles");
        }
    }

   
    static void confirmarPedido() {
        try {
            Pedido pedido = new Pedido(crearUsuario(), List.of(crearDetalle()));
            pedido.confirmar();
            System.out.println("✔ Pedido confirmado correctamente");
        } catch (Exception e) {
            System.out.println("✘ Error al confirmar pedido");
        }
    }


    static void noConfirmarPedidoYaConfirmado() {
        try {
            Pedido pedido = new Pedido(crearUsuario(), List.of(crearDetalle()));
            pedido.confirmar();
            pedido.confirmar();
            System.out.println("✘ ERROR: Se confirmó un pedido dos veces");
        } catch (Exception e) {
            System.out.println("✔ No se permite confirmar un pedido ya confirmado");
        }
    }


    static void cancelarPedidoPendiente() {
        try {
            Pedido pedido = new Pedido(crearUsuario(), List.of(crearDetalle()));
            pedido.cancelar("Cliente canceló");
            System.out.println("✔ Pedido pendiente cancelado");
        } catch (Exception e) {
            System.out.println("✘ Error al cancelar pedido pendiente");
        }
    }

    
    static void noCancelarPedidoConPago() {
        try {
            Pedido pedido = new Pedido(crearUsuario(), List.of(crearDetalle()));
            pedido.confirmar();

            Pago pago = new Pago(30000f, "TARJETA", "REF-001");
            pago.aprobar();
            pedido.registrarPago(pago);

            pedido.cancelar("Intento cancelar");
            System.out.println("✘ ERROR: Se canceló pedido con pago");
        } catch (Exception e) {
            System.out.println("✔ No se permite cancelar pedido con pago");
        }
    }

    
    static void flujoCompletoPedido() {
        try {
            Pedido pedido = new Pedido(crearUsuario(), List.of(crearDetalle()));
            pedido.confirmar();

            Pago pago = new Pago(30000f, "TARJETA", "REF-002");
            pago.aprobar();
            pedido.registrarPago(pago);

            Envio envio = new Envio("CDMX", "GUIA123456");
            pedido.registrarEnvio(envio);

            System.out.println("✔ Flujo completo de pedido exitoso");
        } catch (Exception e) {
            System.out.println("✘ Error en flujo completo: " + e.getMessage());
        }
    }

    private static Usuario crearUsuario() {
        return new Usuario(
                "María López",
                "maria@test.com",
                "hash123",
                Rol.CLIENTE,
                "5512345678"
        );
    }

    private static DetallePedido crearDetalle() {
        return new DetallePedido(
                "PROD-001",
                100,
                new Money(15000f, "MXN"),
                "Laptop"
        );
    }
}
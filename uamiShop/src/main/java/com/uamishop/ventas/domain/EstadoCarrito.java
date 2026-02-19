package com.uamishop.ventas.domain;

public enum EstadoCarrito {
    ACTIVO,     //El usuario puede agregar o quitar productos
    CHECKOUT,   //El usuario está pagando
    COMPLETADO,  //La orden se generó exitosamente
    ABANDONADO    //El usuario abandonó el carrito sin completar la compra
}

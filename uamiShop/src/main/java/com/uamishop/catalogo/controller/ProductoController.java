package com.uamishop.catalogo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.uamishop.catalogo.service.ProductoService;

import java.util.List;
import java.util.UUID;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.uamishop.catalogo.controller.dto.ProductoResponse;
import com.uamishop.catalogo.controller.dto.ProductoRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    // Aquí irán los endpoints relacionados con productos
    // Ejemplo: GET /api/productos/{id} para obtener detalles de un producto
    //Estos metodos harán referencia a los servicios del dominio, que a su vez interactuarán con los repositorios para obtener o modificar datos en la base de datos.

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(ProductoRequest request) {
        ProductoResponse response = productoService.crearProducto(request);
        return ResponseEntity.ok(response);
    }

    /*En esl siguiente metodo utilizamos @pathvariable para tomar el valor embebido de la url
    y pasarlo a la variable que necesita nuestro service en este caso UUID */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> buscarPorId(@PathVariable UUID id) {
        ProductoResponse response = productoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /*En el siguiente metodo se obtienen todos los productos de la base de datos, es por eso
     que no tiene una ruta especifica (porque tener ../productos/productos es redundante) */
    @GetMapping 
    public ResponseEntity<List<ProductoResponse>> buscarTodos() {
        List<ProductoResponse> response = productoService.buscarTodos();
        return ResponseEntity.ok(response);
    }

    // Ahora añadiremos un endpoint para actualizar un producto, utilizando PUT y recibiendo el ID del producto a actualizar en la URL, y los nuevos datos en el cuerpo de la solicitud.
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizarProducto(@PathVariable UUID id, @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    //Seguimos para el metodo de activar un producto 
    @PostMapping("/{id}/activar")
    public ResponseEntity<Void> activarProducto(@PathVariable UUID id) {
        productoService.activar(id);
        return ResponseEntity.ok().build();
    }

    //Y el metodo de desactivar un producto
    /*utilizamos build() para retornar una respuesta sin cuerpo, 
     indicando que la operación fue exitosa con el status 200 OK*/
    @PostMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarProducto(@PathVariable UUID id) {
        productoService.desactivar(id);
        return ResponseEntity.ok().build(); //utilizamos build() para retornar una respuesta sin cuerpo, indicando que la operación fue exitosa con el status 200 OK
    }

}

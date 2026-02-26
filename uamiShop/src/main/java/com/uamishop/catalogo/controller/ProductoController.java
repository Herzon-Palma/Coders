package com.uamishop.catalogo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.uamishop.catalogo.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.uamishop.catalogo.controller.dto.ProductoResponse;
import com.uamishop.ApiError;
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

    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto en el catálogo con los datos proporcionados.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Producto creado exitosamente",
            headers = @Header(
                name = "Location", 
                description = "URL para acceder al producto creado", 
                schema = @Schema(type = "string")
            ),
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos", 
            content = @Content(schema = @Schema(implementation = ApiError.class)) 
        )
    })
    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener producto por ID", description = "Obtiene los detalles de un producto específico utilizando su ID.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Producto encontrado exitosamente", 
            headers = @Header(
                name = "Location", 
                description = "URL para acceder al producto", 
                schema = @Schema(type = "string")
            ),
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Producto no encontrado", 
            content = @Content(schema = @Schema(implementation = ApiError.class)) 
        )
    })
    /*En esl siguiente metodo utilizamos @pathvariable para tomar el valor embebido de la url
    y pasarlo a la variable que necesita nuestro service en este caso UUID */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> buscarPorId(@PathVariable UUID id) {
        ProductoResponse response = productoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener todos los productos", description = "Obtiene una lista de todos los productos disponibles en el catálogo.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Productos obtenidos exitosamente", 
            headers = @Header(
                name = "Location", 
                description = "URL para acceder a los productos", 
                schema = @Schema(type = "string")
            ),
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))
        )
    })
     /*En el siguiente metodo se obtienen todos los productos de la base de datos, es por eso
     que no tiene una ruta especifica (porque tener ../productos/productos es redundante) */
    /*En el siguiente metodo se obtienen todos los productos de la base de datos, es por eso
     que no tiene una ruta especifica (porque tener ../productos/productos es redundante) */
    @GetMapping 
    public ResponseEntity<List<ProductoResponse>> buscarTodos() {
        List<ProductoResponse> response = productoService.buscarTodos();
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Actualizar un producto", description = "Actualiza los datos de un producto existente utilizando su ID.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Producto actualizado exitosamente", 
            headers = @Header(
                name = "Location", 
                description = "URL para acceder al producto actualizado", 
                schema = @Schema(type = "string")
            ),
            content = @Content(schema = @Schema(implementation = ProductoResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos", 
            content = @Content(schema = @Schema(implementation = ApiError.class)) 
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Producto no encontrado", 
            content = @Content(schema = @Schema(implementation = ApiError.class)) 
        )
    })
     /*En el siguiente metodo utilizamos @pathvariable para tomar el valor embebido de la url*/
    // Ahora añadiremos un endpoint para actualizar un producto, utilizando PUT y recibiendo el ID del producto a actualizar en la URL, y los nuevos datos en el cuerpo de la solicitud.
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizarProducto(@PathVariable UUID id, @Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Activar un producto", description = "Activa un producto específico utilizando su ID, haciéndolo disponible para la venta.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Producto activado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Producto no encontrado", 
            content = @Content(schema = @Schema(implementation = ApiError.class)) 
        )
    })
     /*En el siguiente metodo utilizamos @pathvariable para tomar el valor embebido de la url*/
     //Seguimos para el metodo de activar un producto
    //Seguimos para el metodo de activar un producto 
    @PostMapping("/{id}/activar")
    public ResponseEntity<Void> activarProducto(@PathVariable UUID id) {
        productoService.activar(id);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Desactivar un producto", description = "Desactiva un producto específico utilizando su ID, haciéndolo no disponible para la venta.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Producto desactivado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Producto no encontrado", 
            content = @Content(schema = @Schema(implementation = ApiError.class)) 
        )
    })
     /*En el siguiente metodo utilizamos @pathvariable para tomar el valor embebido de la url*/
    //Y el metodo de desactivar un producto
    /*utilizamos build() para retornar una respuesta sin cuerpo, 
     indicando que la operación fue exitosa con el status 200 OK*/
    @PostMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarProducto(@PathVariable UUID id) {
        productoService.desactivar(id);
        return ResponseEntity.ok().build(); //utilizamos build() para retornar una respuesta sin cuerpo, indicando que la operación fue exitosa con el status 200 OK
    }

}

package com.uamishop.catalogo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.uamishop.catalogo.controller.dto.CategoriaRequest;
import com.uamishop.catalogo.controller.dto.CategoriaResponse;
import com.uamishop.catalogo.service.CategoriaService;
import com.uamishop.ApiError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/* 
 En este controller se encarga de exponer los metodos a treves de las
 Api's.
 Se utilizan etiquetas cómo:
    @PathVariable para tomar el valor embebido en la url y "transformarlo" 
    para que nuestro servicio lo tome cómo debe de ser
    
    @RequestBody Convierte automáticamente el payload JSON en un objeto Java
*/

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Catálogo: Categorías", description = "Operaciones relacionadas con la gestión de categorías de productos")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Crear una nueva categoría", description = "Crea una nueva categoría en el catálogo con los datos proporcionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente", headers = @Header(name = "Location", description = "URL para acceder a la categoría creada", schema = @Schema(type = "string")), content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<CategoriaResponse> crearCategoria(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse response = categoriaService.crearCategoria(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener todas las categorías", description = "Obtiene una lista de todas las categorías disponibles en el catálogo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorías obtenidas exitosamente", headers = @Header(name = "Location", description = "URL para acceder a las categorías", schema = @Schema(type = "string")), content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    })
    /*
     * En el siguiente metodo se obtienen todas las categorias de la base de datos,
     * es por eso
     * que no tiene una ruta especifica (porque tener ../categorias/categorias es
     * redundante)
     */
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> buscarTodasCategorias() {
        List<CategoriaResponse> response = categoriaService.buscarTodasCategorias();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar una categoría existente", description = "Actualiza los detalles de una categoría específica utilizando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente", headers = @Header(name = "Location", description = "URL para acceder a la categoría actualizada", schema = @Schema(type = "string")), content = @Content(schema = @Schema(implementation = CategoriaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    /*
     * En el siguiente metodo se actualiza una categoria de la base de datos, es por
     * eso
     * que tiene una ruta especifica (porque se necesita el id para actualizar la
     * categoria)
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizarCategoria(@PathVariable UUID id,
            @Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse response = categoriaService.actualizarCategoria(id, request);
        return ResponseEntity.ok(response);
    }

}

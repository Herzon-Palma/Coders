package com.uamishop.catalogo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.uamishop.catalogo.controller.dto.CategoriaRequest;
import com.uamishop.catalogo.controller.dto.CategoriaResponse;
import com.uamishop.catalogo.service.CategoriaService;

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

public class CategoriaController {
    private final CategoriaService categoriaService;
    
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> crearCategoria(CategoriaRequest request) {
        CategoriaResponse response = categoriaService.crearCategoria(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> buscarTodasCategorias() {
        List<CategoriaResponse> response = categoriaService.buscarTodasCategorias();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizarCategoria(@PathVariable UUID id, @RequestBody CategoriaRequest request) {
        CategoriaResponse response = categoriaService.actualizarCategoria(id, request);
        return ResponseEntity.ok(response);
    }
    
    
}

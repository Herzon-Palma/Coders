-- Script de inicialización de la base de datos del microservicio catálogo.
-- Este script es ejecutado por MySQL al iniciar el contenedor por primera vez.
-- Crea la base de datos y el schema si no existen.

CREATE DATABASE IF NOT EXISTS uamishop_catalogo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE uamishop_catalogo;

INSERT INTO imagenes (url, orden) VALUES ('https://images.unsplash.com/photo-1523275335684-37898b6baf30?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1170&q=80', 1);


CREATE SCHEMA IF NOT EXISTS catalogo;

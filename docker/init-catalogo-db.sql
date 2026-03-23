-- Script de inicialización de la base de datos del microservicio catálogo.
-- Este script es ejecutado por MySQL al iniciar el contenedor por primera vez.
-- Crea la base de datos y el schema si no existen.

CREATE DATABASE IF NOT EXISTS uamishop_catalogo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE uamishop_catalogo;

CREATE SCHEMA IF NOT EXISTS catalogo;

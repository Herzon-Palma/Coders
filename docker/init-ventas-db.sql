CREATE DATABASE IF NOT EXISTS uamishop_ventas;
CREATE USER IF NOT EXISTS 'uamishop'@'%' IDENTIFIED BY 'uamishop';
GRANT ALL PRIVILEGES ON uamishop_ventas.* TO 'uamishop'@'%';
FLUSH PRIVILEGES;

USE uamishop_ventas;
CREATE SCHEMA IF NOT EXISTS ventas;

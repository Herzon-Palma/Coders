CREATE DATABASE IF NOT EXISTS uamishop_ordenes CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE uamishop_ordenes;
CREATE SCHEMA IF NOT EXISTS ordenes;
GRANT ALL PRIVILEGES ON uamishop_ordenes.* TO 'uamishop'@'%';

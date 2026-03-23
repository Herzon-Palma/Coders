CREATE DATABASE IF NOT EXISTS uamishop_catalogo;
GRANT ALL PRIVILEGES ON uamishop_catalogo.* TO 'uamishop'@'%';

CREATE DATABASE IF NOT EXISTS uamishop_ventas;
GRANT ALL PRIVILEGES ON uamishop_ventas.* TO 'uamishop'@'%';

CREATE DATABASE IF NOT EXISTS uamishop_ordenes;
GRANT ALL PRIVILEGES ON uamishop_ordenes.* TO 'uamishop'@'%';

FLUSH PRIVILEGES;

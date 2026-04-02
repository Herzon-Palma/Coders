# Proyecto Tienda UAM-I: Migración a Microservicios (Catálogo, Órdenes y Ventas)

Este proyecto consiste en un entorno de microservicios desglosado desde un monolito inicial (`uamiShop`). Consta de cuatro partes principales:

- **Monolito (`uamiShop`):** Ejecutándose en el puerto 8080. Aún maneja algunas lógicas legacy.
- **Catálogo (`uamishop-catalogo`):** Microservicio en el puerto 8081.
- **Órdenes (`uamishop-ordenes`):** Microservicio en el puerto 8082.
- **Ventas/Carrito (`uamishop-ventas`):** Microservicio en el puerto 8083.
- **Infraestructura:** Una base de datos MySQL centralizada (con esquemas por microservicio) y el manejador de mensajería RabbitMQ.

---

### ¿Cómo correr todo el proyecto usando Docker? (Recomendado)

La forma más sencilla de correr toda la base de datos, RabbitMQ y los 4 microservicios en paralelo es utilizar **Docker Compose**. 
Hemos configurado los `Dockerfile` de cada servicio para que **se omitan los tests** usando la bandera `-Dmaven.test.skip=true`. Esto asegura que los microservicios se compilen correctamente.

Abre **una sola terminal** en la raíz del proyecto (`Coders/`) y ejecuta:

```bash
# Iniciar docker (si no está iniciado)
sudo systemctl start docker

# Compilar y arrancar todos los servicios en paralelo
docker compose up --build -d
```

Todos los servicios se construirán, compilarán y funcionarán en paralelo en segundo plano. Para revisar si ya están funcionando o si quieres detenerlos:

```bash
# Ver los contenedores corriendo
docker compose ps

# Ver logs de un contenedor en específico (ej. ventas)
docker compose logs -f uamishop-ventas

# Detener todos los servicios
docker compose down
```

---

### ¿Cómo correr los microservicios de forma manual por Terminales?

Si necesitas depurar los servicios levantándolos uno por uno sin usar Docker Compose para tu código, puedes hacerlo asegurándote de usar la bandera que evita compilar tests que estén obsoletos.

**Terminal 1 (Infraestructura):**
```bash
sudo systemctl start docker
docker compose up mysql rabbitmq -d
```

**Terminal 2 (Monolito):**
```bash
cd uamiShop
mvn spring-boot:run -Dspring-boot.run.profiles=mysql -Dmaven.test.skip=true
```

**Terminal 3 (Catálogo):**
```bash
cd uamishop-catalogo
mvn spring-boot:run -Dmaven.test.skip=true
```

**Terminal 4 (Órdenes):**
```bash
cd uamishop-ordenes
mvn spring-boot:run -Dmaven.test.skip=true
```

**Terminal 5 (Ventas):**
```bash
cd uamishop-ventas
mvn spring-boot:run -Dmaven.test.skip=true
```

---

### Documentación de las API y Servicios

Una vez que el proyecto esté corriendo exitosamente (ya sea vía Docker o vía local), puedes entrar a las siguientes direcciones:

| Servicio | URL Local | Descripción |
| :--- | :--- | :--- |
| **Monolito Swagger** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | APIs legadas y no migradas todavía |
| **Catálogo Swagger** | [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html) | API independiente del catálogo |
| **Órdenes Swagger** | [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html) | API de creación y consulta de órdenes |
| **Ventas Swagger** | [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html) | API de carritos de compras y checkout |
| **RabbitMQ Admin** | [http://localhost:15672](http://localhost:15672) | Dashboard de mensajería (User: `guest`, Pass: `guest`) |
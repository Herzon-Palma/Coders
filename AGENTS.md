# Contexto del Proyecto y Reglas para el Agente (AI)

Eres un desarrollador experto en Java, Spring Boot, Diseño Guiado por Dominio (DDD) táctico y estratégico, y arquitectura de Monolito Modular. Estás asistiendo en el desarrollo de un sistema de e-commerce (UAMIShop) para un curso de ingeniería de software.

Tu objetivo es escribir código que cumpla **estrictamente** con la arquitectura y convenciones descritas a continuación. No inventes atajos ni rompas la separación de responsabilidades.

## 1. Arquitectura Base: Monolito Modular
El sistema está construido como un único proyecto Spring Boot pero separado lógicamente en módulos (Bounded Contexts):
- `catalogo` (Dominio Core)
- `ventas` (Dominio Core)
- `ordenes` (Dominio Core)
- `shared` (Código y Value Objects compartidos)

**Regla de Oro de la Modularidad:** Un módulo no puede acceder a las clases internas (`domain`, `repository`) de otro módulo. Si el módulo de `ordenes` necesita algo de `ventas`, debe hacerlo a través de una API interna o a través de los Servicios, nunca instanciando el repositorio de otro Bounded Context ni entidades completas. Entre dominios distribuidos deben pasarse Referencias (Ids).

## 2. Estructura de Paquetes
Dentro de cada Bounded Context (ej. `com.uamishop.catalogo`), la estructura debe ser:
- `api/` (Opcional): Interfaces de contrato público y DTOs inter-módulo para consumo de otros dominios de manera limpia.
- `controller/`: Capa de presentación (API externa). Contiene clases `@RestController` y el subpaquete `dto/` (Records para request/response) y la documentación de Swagger/OpenAPI.
- `service/`: Capa de servicio/aplicación. Orquesta la lógica de negocio, coordina agregados y maneja transacciones (`@Transactional`).
- `domain/`: Capa de dominio. Contiene Aggregate Roots, Entities, Value Objects y un subpaquete `exception/` para excepciones puras de negocio. **No tiene dependencias de infraestructura ni de Spring Web. Es puramente código Java.**
- `repository/`: Capa de persistencia. Contiene las interfaces de Spring Data JPA (`@Repository`). **Solo existe un repositorio por cada Aggregate Root.**

## 3. Patrones de Diseño Guiado por Dominio (DDD Táctico)
- **Value Objects:** Deben implementarse obligatoriamente usando `record` de Java (introducido en Java 16+). Son inmutables, se comparan por valor y auto-validan sus datos en constructores compactos. Ejemplo: `Money`, `ProductoId`. Evitar la obsesión por los primitivos.
- **Entities:** Clases mutables con identidad única (Id).
- **Aggregate Roots:** La entidad principal de una transacción. La lógica de negocio y validación de invariantes ocurre **dentro** de sus métodos. El exterior (Servicios) invoca estos métodos, no hace setters directos de datos.
- **Mapeo de Persistencia:** Los atributos de las entidades usan anotaciones de persistencia (`JPA/Hibernate` como `@Entity`, `@Id`, `@Embedded`) para evitar la sobre-duplicación de modelos.
- **Servicios de Dominio / Aplicación:** Clases `@Service`. No contienen lógica de negocio core (eso va en el Agregado). Solo orquestan: 1) Buscar en repositorio, 2) Invocar método del Agregado, 3) Guardar en repositorio. Establecer fronteras transaccionales manejando anotaciones **`@Transactional`** a nivel de la capa de servicio (nunca en el controller).

## 4. Desarrollo de APIs REST
- **Controladores:** Anotados con `@RestController` y `@RequestMapping`. No contienen lógica de negocio. Solo reciben DTOs, llaman al `Service`, y devuelven DTOs en un `ResponseEntity`.
- **Mapeo DTO <-> Entidad:** Las entidades de dominio **nunca** deben filtrarse en las respuestas HTTP. Siempre se mapean a clases `record` tipo `...Response` y se reciben mediante `...Request`. Utilizar **Records** de Java de preferencia para los `Request` y `Response` DTOs.
- **Verbos HTTP:** 
  - `POST` para crear (devuelve `201 Created` y header `Location`).
  - `GET` para recuperar (devuelve `200 OK`).
  - `PUT` para actualización completa / `PATCH` para actualización parcial.
  - `DELETE` para eliminar o cambiar estado a inactivo (dependiendo la regla).
- **Validaciones:** Usar `@Valid` en los métodos del controlador y anotaciones de `jakarta.validation.constraints.*` (`@NotBlank`, `@Positive`, `@NotNull`, `@Size`, etc.) en los DTOs de Request.
- **Documentación Swagger/OpenAPI:** Todos los endpoints deben estar documentados usando las anotaciones de `springdoc-openapi`: `@Operation`, `@ApiResponses`, `@ApiResponse`, etc. Y poseer de preferencia una configuración base/global.

## 5. Manejo de Errores Globales
- Las validaciones de negocio fallidas dentro del dominio deben lanzar excepciones personalizadas que heredan de una base (ej. `DomainException` o `BusinessRuleException`).
- Las reglas técnicas de entrada generan `MethodArgumentNotValidException`.
- **NUNCA usar `try-catch` dentro de los `Controllers`**. Todo el manejo de errores debe estar centralizado en una clase global anotada con `@RestControllerAdvice` y métodos `@ExceptionHandler` que traduzcan excepciones a un JSON estándar de error (`ApiError`) con el `status` HTTP adecuado (`400` para errores cliente y validaciones, `404` para no encontrado, `409` para conflictos, `422` Unprocessable Entity para reglas de negocio rotas, `500` interno).

## 6. Pruebas
- **Unitarias:** Prueban la lógica de negocio en los Aggregate Roots y Value Objects usando JUnit 5 pura, sin cargar contexto de Spring.
- **Integración:** Prueban la conexión del controlador hasta la base de datos (generalmente H2 en memoria). Usan `@SpringBootTest` con puerto aleatorio y realizan llamadas HTTP con `TestRestTemplate`. Se verifica el código de estado, los encabezados (ej. `Location`) y el cuerpo de la respuesta. Se deben limpiar los repositorios entre ejecuciones.

---
**Nota para el Agente:** AL PROGRAMAR, tu única responsabilidad técnica es apegarte a las reglas descritas anteriormente. No inventes arquitecturas. Apégate puramente a DDD, modularidad limpia y el uso eficiente de Spring Boot con Swagger, Validaciones, y Manejo Global de Errores. No modifiques código existente de manera disruptiva sin informar explícitamente la razón arquitectónica detrás de la decisión.

uamiShop
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── uamishop
│   │   │           ├── UamiShopApplication.java
│   │   │           │
│   │   │           ├── shared
│   │   │           │   └── domain
│   │   │           │       ├── event
│   │   │           │       │   ├── DomainEvent.java
│   │   │           │       │   └── DomainEventPublisher.java
│   │   │           │       ├── exception
│   │   │           │       │   ├── DomainException.java
│   │   │           │       │   └── BusinessRuleViolation.java
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── Money.java
│   │   │           │       │   ├── Email.java
│   │   │           │       │   └── Address.java
│   │   │           │       └── util
│   │   │           │           └── IdGenerator.java
│   │   │           │
│   │   │           ├── ventas
│   │   │           │   └── domain
│   │   │           │       ├── aggregate
│   │   │           │       │   ├── Carrito.java                 # Aggregate Root
│   │   │           │       │   └── ItemCarrito.java
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── CarritoId.java
│   │   │           │       │   ├── ProductoId.java              # Ref por identidad, no entidad externa
│   │   │           │       │   ├── UsuarioId.java
│   │   │           │       │   ├── Cantidad.java
│   │   │           │       │   └── PrecioUnitario.java
│   │   │           │       ├── event
│   │   │           │       │   ├── ItemAgregadoAlCarrito.java
│   │   │           │       │   ├── ItemEliminadoDelCarrito.java
│   │   │           │       │   └── CarritoConfirmadoParaCheckout.java
│   │   │           │       ├── repository
│   │   │           │       │   └── CarritoRepository.java        # interfaz
│   │   │           │       └── service
│   │   │           │           └── CarritoDomainService.java      # reglas cruzadas del agregado
│   │   │           │
│   │   │           ├── checkout
│   │   │           │   └── domain
│   │   │           │       ├── aggregate
│   │   │           │       │   └── Checkout.java                 # Aggregate Root
│   │   │           │       ├── entity
│   │   │           │       │   └── IntentoPago.java              # si modelas intentos dentro del checkout
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── CheckoutId.java
│   │   │           │       │   ├── CarritoId.java
│   │   │           │       │   ├── DireccionEnvio.java
│   │   │           │       │   ├── DatosContacto.java
│   │   │           │       │   ├── MetodoPago.java               # enum/valueobject
│   │   │           │       │   └── CodigoCupon.java
│   │   │           │       ├── policy
│   │   │           │       │   ├── StockValidationPolicy.java    # contrato hacia Inventario (solo interfaz)
│   │   │           │       │   ├── CouponPolicy.java             # contrato hacia Promociones
│   │   │           │       │   └── PaymentPolicy.java            # contrato hacia Pagos
│   │   │           │       ├── event
│   │   │           │       │   ├── CheckoutIniciado.java
│   │   │           │       │   ├── CheckoutPagado.java
│   │   │           │       │   ├── CheckoutFallido.java
│   │   │           │       │   └── OrdenSolicitada.java          # “dispara” creación de orden
│   │   │           │       ├── repository
│   │   │           │       │   └── CheckoutRepository.java
│   │   │           │       └── service
│   │   │           │           └── CheckoutDomainService.java
│   │   │           │
│   │   │           ├── ordenes
│   │   │           │   └── domain
│   │   │           │       ├── aggregate
│   │   │           │       │   ├── Orden.java                   # Aggregate Root
│   │   │           │       │   └── ItemOrden.java
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── OrdenId.java
│   │   │           │       │   ├── UsuarioId.java
│   │   │           │       │   ├── DireccionEnvio.java
│   │   │           │       │   ├── OrdenStatus.java             # enum/valueobject
│   │   │           │       │   └── PaymentStatus.java           # enum/valueobject
│   │   │           │       ├── event
│   │   │           │       │   ├── OrdenCreada.java
│   │   │           │       │   ├── OrdenCancelada.java
│   │   │           │       │   ├── OrdenEnviada.java
│   │   │           │       │   └── OrdenEntregada.java
│   │   │           │       ├── repository
│   │   │           │       │   └── OrdenRepository.java
│   │   │           │       └── service
│   │   │           │           └── OrdenDomainService.java
│   │   │           │
│   │   │           ├── catalogo
│   │   │           │   └── domain
│   │   │           │       ├── aggregate
│   │   │           │       │   └── Producto.java                # Aggregate Root (soporte)
│   │   │           │       ├── entity
│   │   │           │       │   └── Categoria.java
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── ProductoId.java
│   │   │           │       │   ├── NombreProducto.java
│   │   │           │       │   ├── Descripcion.java
│   │   │           │       │   ├── Imagen.java
│   │   │           │       │   └── Precio.java                  # envuelve Money
│   │   │           │       ├── repository
│   │   │           │       │   └── ProductoRepository.java
│   │   │           │       └── event
│   │   │           │           └── ProductoActualizado.java
│   │   │           │
│   │   │           ├── inventario
│   │   │           │   └── domain
│   │   │           │       ├── aggregate
│   │   │           │       │   └── Stock.java                   # Aggregate Root
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── ProductoId.java
│   │   │           │       │   ├── Cantidad.java
│   │   │           │       │   └── StockStatus.java             # en stock/agotado
│   │   │           │       ├── event
│   │   │           │       │   ├── StockReservado.java
│   │   │           │       │   ├── StockDecrementado.java
│   │   │           │       │   └── StockInsuficienteDetectado.java
│   │   │           │       ├── repository
│   │   │           │       │   └── StockRepository.java
│   │   │           │       └── service
│   │   │           │           └── InventarioDomainService.java
│   │   │           │
│   │   │           ├── promociones
│   │   │           │   └── domain
│   │   │           │       ├── aggregate
│   │   │           │       │   ├── Promocion.java               # Aggregate Root
│   │   │           │       │   └── Cupon.java                   # puede ser entity del agregado
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── CuponId.java
│   │   │           │       │   ├── CodigoCupon.java
│   │   │           │       │   ├── Vigencia.java
│   │   │           │       │   └── ReglaDescuento.java
│   │   │           │       ├── event
│   │   │           │       │   ├── CuponValidado.java
│   │   │           │       │   └── DescuentoCalculado.java
│   │   │           │       ├── repository
│   │   │           │       │   └── PromocionRepository.java
│   │   │           │       └── service
│   │   │           │           └── PromocionesDomainService.java
│   │   │           │
│   │   │           ├── identidad
│   │   │           │   └── domain
│   │   │           │       ├── aggregate
│   │   │           │       │   └── Usuario.java                 # Aggregate Root
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── UsuarioId.java
│   │   │           │       │   ├── Rol.java                     # enum/valueobject
│   │   │           │       │   ├── Email.java
│   │   │           │       │   └── PasswordHash.java
│   │   │           │       ├── event
│   │   │           │       │   ├── UsuarioRegistrado.java
│   │   │           │       │   └── SesionIniciada.java
│   │   │           │       └── repository
│   │   │           │           └── UsuarioRepository.java
│   │   │           │
│   │   │           ├── pagos
│   │   │           │   └── domain
│   │   │           │       ├── aggregate
│   │   │           │       │   └── Pago.java                    # Aggregate Root
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── PagoId.java
│   │   │           │       │   ├── OrdenId.java
│   │   │           │       │   ├── Money.java
│   │   │           │       │   └── PaymentStatus.java
│   │   │           │       ├── event
│   │   │           │       │   ├── PagoProcesado.java
│   │   │           │       │   └── PagoRechazado.java
│   │   │           │       └── repository
│   │   │           │           └── PagoRepository.java
│   │   │           │
│   │   │           ├── envios
│   │   │           │   └── domain
│   │   │           │       ├── aggregate
│   │   │           │       │   └── Envio.java                   # Aggregate Root
│   │   │           │       ├── valueobject
│   │   │           │       │   ├── EnvioId.java
│   │   │           │       │   ├── OrdenId.java
│   │   │           │       │   ├── TrackingNumber.java
│   │   │           │       │   └── ShippingStatus.java
│   │   │           │       ├── event
│   │   │           │       │   ├── EnvioSolicitado.java
│   │   │           │       │   └── EstadoEnvioActualizado.java
│   │   │           │       └── repository
│   │   │           │           └── EnvioRepository.java
│   │   │           │
│   │   │           └── notificaciones
│   │   │               └── domain
│   │   │                   ├── aggregate
│   │   │                   │   └── Notificacion.java            # Aggregate Root (si persistes notifs)
│   │   │                   ├── valueobject
│   │   │                   │   ├── NotificacionId.java
│   │   │                   │   ├── UsuarioId.java
│   │   │                   │   ├── Canal.java                   # EMAIL/SMS/PUSH
│   │   │                   │   └── Plantilla.java
│   │   │                   ├── event
│   │   │                   │   └── NotificacionEnviada.java
│   │   │                   └── repository
│   │   │                       └── NotificacionRepository.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── com
│               └── uamishop
│                   ├── ventas
│                   │   └── domain
│                   │       └── CarritoTest.java
│                   ├── checkout
│                   │   └── domain
│                   │       └── CheckoutTest.java
│                   ├── ordenes
│                   │   └── domain
│                   │       └── OrdenTest.java
│                   ├── catalogo
│                   │   └── domain
│                   │       └── ProductoTest.java
│                   ├── inventario
│                   │   └── domain
│                   │       └── StockTest.java
│                   ├── promociones
│                   │   └── domain
│                   │       └── PromocionTest.java
│                   ├── identidad
│                   │   └── domain
│                   │       └── UsuarioTest.java
│                   ├── pagos
│                   │   └── domain
│                   │       └── PagoTest.java
│                   ├── envios
│                   │   └── domain
│                   │       └── EnvioTest.java
│                   └── notificaciones
│                       └── domain
│                           └── NotificacionTest.java
└── .gitignore

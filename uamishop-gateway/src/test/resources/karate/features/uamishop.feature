Feature: Probar las rutas de API Gateway

    Background:
        * url 'http://localhost:9000'

    Scenario: Probar ruta de catalogo existe
        Given path '/api/v1/productos'
        When method GET
        Then status 200

    Scenario: Crear un carrito
        Given path '/api/v1/carritos'
        And header Content-Type = 'application/json'
        And request { clienteId: "123e4567-e89b-12d3-a456-426614174000" }
        When method POST
        Then status 201

    Scenario: Obtener una orden que no existe debe regresar 404
        Given path '/api/v1/ordenes/123e4567-e89b-12d3-a456-426614174000'
        When method GET
        Then status 404

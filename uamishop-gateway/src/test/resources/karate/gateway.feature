Feature: Pruebas automatizadas del API Gateway para uamiShop

  Background:
    # URL base del API Gateway
    * url 'http://localhost:8080'

  Scenario: Obtener la lista de productos a través del Gateway (Catálogo)
    Given path '/catalogo/api/v1/productos'
    When method GET
    Then status 200
    And match response == '#array'

  Scenario: Crear un carrito a través del Gateway (Ventas)
    Given path '/ventas/api/v1/carritos'
    And request { "id": "123e4567-e89b-12d3-a456-426614174000" }
    When method POST
    Then status 200
    And match response.id == '#uuid'
    And match response.clienteId == '123e4567-e89b-12d3-a456-426614174000'
    And match response.items == '#array'
    And match response.estado == 'ACTIVO'

  Scenario: Intentar crear una orden con datos inválidos (Órdenes)
    Given path '/ordenes/api/v1/ordenes'
    And request { "clienteId": "123e4567-e89b-12d3-a456-426614174000", "direccion": null }
    When method POST
    Then status 400

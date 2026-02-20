package com.uamishop.shared.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object compartido: Dirección de envío.
 * Campos alineados con el diagrama de clases del SubDominio Orden.
 * - RN-VO-03: Todos los campos son obligatorios excepto "instrucciones".
 * - RN-VO-04: El país debe ser "México".
 * - RN-ORD-03: Código postal debe ser de 5 dígitos.
 * - RN-ORD-04: Teléfono de contacto debe ser de 10 dígitos.
 */
public record DireccionEnvio(
        String nombreDestinatario,
        String calle,
        String ciudad,
        String estado,
        String codigoPostal,
        String pais,
        String telefono,
        String instrucciones) {

    private static final Pattern CP_5 = Pattern.compile("^\\d{5}$");
    private static final Pattern TEL_10 = Pattern.compile("^\\d{10}$");

    public DireccionEnvio {
        // RN-VO-03: todos obligatorios excepto instrucciones
        Objects.requireNonNull(nombreDestinatario, "Nombre destinatario requerido");
        Objects.requireNonNull(calle, "Calle requerida");
        Objects.requireNonNull(ciudad, "Ciudad requerida");
        Objects.requireNonNull(estado, "Estado requerido");
        Objects.requireNonNull(codigoPostal, "Código postal requerido");
        Objects.requireNonNull(pais, "País requerido");
        Objects.requireNonNull(telefono, "Teléfono requerido");

        if (nombreDestinatario.isBlank()) {
            throw new IllegalArgumentException("Nombre destinatario no puede estar vacío");
        }
        if (calle.isBlank()) {
            throw new IllegalArgumentException("Calle no puede estar vacía");
        }
        if (ciudad.isBlank()) {
            throw new IllegalArgumentException("Ciudad no puede estar vacía");
        }
        if (estado.isBlank()) {
            throw new IllegalArgumentException("Estado no puede estar vacío");
        }
        if (codigoPostal.isBlank()) {
            throw new IllegalArgumentException("Código postal no puede estar vacío");
        }
        if (pais.isBlank()) {
            throw new IllegalArgumentException("País no puede estar vacío");
        }
        if (telefono.isBlank()) {
            throw new IllegalArgumentException("Teléfono no puede estar vacío");
        }

        // RN-ORD-03
        if (!CP_5.matcher(codigoPostal).matches()) {
            throw new IllegalArgumentException(
                    "El código postal debe contener exactamente 5 dígitos");
        }
        // RN-ORD-04
        if (!TEL_10.matcher(telefono).matches()) {
            throw new IllegalArgumentException(
                    "El teléfono debe contener exactamente 10 dígitos");
        }
        // RN-VO-04
        if (!pais.equals("México")) {
            throw new IllegalArgumentException("El país debe ser México");
        }
    }

    /**
     * Formatea la dirección en una sola cadena legible.
     */
    public String formatear() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombreDestinatario).append("\n");
        sb.append(calle).append("\n");
        sb.append(ciudad).append(", ").append(estado).append(" C.P. ").append(codigoPostal).append("\n");
        sb.append(pais).append("\n");
        sb.append("Tel: ").append(telefono);
        if (instrucciones != null && !instrucciones.isBlank()) {
            sb.append("\nInstrucciones: ").append(instrucciones);
        }
        return sb.toString();
    }
}

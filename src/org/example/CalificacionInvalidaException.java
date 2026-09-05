package org.example;

/**
 * Excepción verificada que representa una calificación fuera del rango
 * válido de 0 a 100 dentro del dominio de la aplicación.
 */
public class CalificacionInvalidaException extends Exception {

    /**
     * Construye la excepción con un mensaje descriptivo del problema.
     *
     * @param mensaje detalle del error, incluyendo el valor rechazado
     */
    public CalificacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
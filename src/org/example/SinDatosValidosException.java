package org.example;

/**
 * Excepción verificada que indica que el archivo procesado no contenía
 * ninguna calificación válida, por lo que no es posible calcular un promedio.
 */
public class SinDatosValidosException extends Exception {

    /**
     * Construye la excepción con un mensaje descriptivo del problema.
     *
     * @param mensaje detalle del error
     */
    public SinDatosValidosException(String mensaje) {
        super(mensaje);
    }
}
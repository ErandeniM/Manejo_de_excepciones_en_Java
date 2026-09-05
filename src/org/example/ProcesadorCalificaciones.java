package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ProcesadorCalificaciones {

    public static void main(String[] args) {

        try {

            procesarArchivo("calificaciones.txt");

        } catch (FileNotFoundException e) {

            System.err.println("No se encontró el archivo.");

        } catch (IOException e) {

            System.err.println(
                    "No fue posible procesar el archivo: " + e.getMessage()
            );
        }
    }

    public static void procesarArchivo(String nombreArchivo)
            throws IOException {

        try (
                BufferedReader lector =
                        new BufferedReader(
                                new FileReader(nombreArchivo)
                        )
        ) {

            String linea;

            while ((linea = lector.readLine()) != null) {

                try {

                    validarLinea(linea);

                    int calificacion = Integer.parseInt(linea.trim());

                    validarCalificacion(calificacion);

                    System.out.println("Calificación válida: " + calificacion);

                } catch (NumberFormatException e) {

                    System.err.println(
                            "Formato numérico inválido: " + e.getMessage()
                    );

                } catch (IllegalArgumentException e) {

                    System.err.println(
                            "Argumento inválido: " + e.getMessage()
                    );
                }
            }
        }
    }

    public static void validarLinea(String linea) {

        if (linea.isBlank()) {

            throw new IllegalArgumentException(
                    "La línea no puede estar vacía."
            );
        }
    }

    public static void validarCalificacion(int calificacion) {

        if (calificacion < 0) {

            throw new IllegalArgumentException(
                    "La calificación no puede ser negativa: " + calificacion
            );
        }

        if (calificacion > 100) {

            throw new IllegalArgumentException(
                    "La calificación no puede exceder 100: " + calificacion
            );
        }
    }
}
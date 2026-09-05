package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ProcesadorCalificaciones {

    public static void main(String[] args) {

        try (
                BufferedReader lector =
                        new BufferedReader(
                                new FileReader("calificaciones.txt")
                        )
        ) {

            String linea;

            while ((linea = lector.readLine()) != null) {

                try {
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

        } catch (FileNotFoundException e) {
            System.err.println("No se encontró el archivo.");

        } catch (IOException e) {
            System.err.println(
                    "Error al leer el archivo: " + e.getMessage()
            );
        }
    }

    public static void validarCalificacion(int calificacion) {
        if (calificacion < 0 || calificacion > 100) {
            throw new IllegalArgumentException(
                    "La calificación debe estar entre 0 y 100: " + calificacion
            );
        }
    }
}
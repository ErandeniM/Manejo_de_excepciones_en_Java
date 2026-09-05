package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ProcesadorCalificaciones {

    public static void main(String[] args) {

        BufferedReader lector = null;

        try {
            lector = new BufferedReader(
                    new FileReader("calificaciones.txt")
            );

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
                    "Error de entrada/salida: " + e.getMessage()
            );

        } finally {
            if (lector != null) {
                try {
                    lector.close();
                    System.out.println("Archivo cerrado correctamente.");
                } catch (IOException e) {
                    System.err.println("No fue posible cerrar el archivo.");
                }
            } else {
                System.out.println("No había archivo que cerrar.");
            }
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
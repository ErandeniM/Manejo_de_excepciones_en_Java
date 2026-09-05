package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcesadorCalificaciones {
    public static void main(String[] args) throws IOException {

        BufferedReader lector =
        new BufferedReader(
                new FileReader("calificaciones.txt")
        );

        String linea;

        while ((linea = lector.readLine()) != null) {
            int calificacion = Integer.parseInt(linea);
            System.out.println(calificacion);
        }

        lector.close();
    }
}

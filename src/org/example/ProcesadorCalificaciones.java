package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Procesa un archivo de texto con calificaciones y calcula su promedio,
 * manejando de forma controlada los datos inválidos que pueda contener.
 * El nombre del archivo se recibe como argumento de línea de comandos.
 *
 * @author Erandeni
 * @version 2.0
 */
public class ProcesadorCalificaciones {

    /**
     * Punto de entrada de la aplicación. Verifica que se haya proporcionado
     * el nombre del archivo y atiende los errores que puedan surgir durante
     * su procesamiento.
     *
     * @param args argumentos de línea de comandos; se espera la ruta del
     *             archivo de calificaciones en la posición cero
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Uso:");
            System.err.println("java ProcesadorCalificaciones <archivo>");
            return;
        }

        String archivo = args[0];

        try {

            double promedio = calcularPromedio(archivo);

            System.out.printf("%nPromedio de valores válidos: %.2f%n", promedio);

        } catch (FileNotFoundException e) {

            System.err.println("No se encontró el archivo: " + archivo);

        } catch (IOException e) {

            System.err.println(
                    "Error de lectura en el archivo: " + e.getMessage()
            );

        } catch (SinDatosValidosException e) {

            System.err.println(e.getMessage());
        }
    }

    /**
     * Lee un archivo de calificaciones y calcula el promedio de los
     * valores válidos. Las líneas vacías, los datos no numéricos y las
     * calificaciones fuera del rango 0-100 se informan y se descartan,
     * sin interrumpir el procesamiento del resto del archivo.
     *
     * @param archivo ruta del archivo de texto que se desea procesar
     * @return el promedio de las calificaciones válidas encontradas
     * @throws java.io.FileNotFoundException si el archivo no existe o no
     *         puede abrirse para lectura
     * @throws IOException si ocurre un error durante la lectura del archivo
     * @throws SinDatosValidosException si el archivo no contiene ninguna
     *         calificación válida
     */
    public static double calcularPromedio(String archivo)
            throws IOException, SinDatosValidosException {

        double suma = 0;
        int contador = 0;

        try (
                BufferedReader lector =
                        new BufferedReader(
                                new FileReader(archivo)
                        )
        ) {

            String linea;

            while ((linea = lector.readLine()) != null) {

                try {

                    validarLinea(linea);

                    int calificacion = Integer.parseInt(linea.trim());

                    validarCalificacion(calificacion);

                    System.out.println("Calificación válida: " + calificacion);

                    suma += calificacion;
                    contador++;

                } catch (NumberFormatException e) {

                    System.err.println("Dato ignorado: " + linea);

                } catch (CalificacionInvalidaException e) {

                    System.err.println(e.getMessage());

                } catch (IllegalArgumentException e) {

                    System.err.println("Dato ignorado: " + e.getMessage());
                }
            }
        }

        if (contador == 0) {
            throw new SinDatosValidosException(
                    "El archivo no contiene ninguna calificación válida: "
                            + archivo
            );
        }

        return suma / contador;
    }

    /**
     * Verifica que una línea del archivo contenga algún carácter distinto
     * de espacio en blanco.
     *
     * @param linea texto leído del archivo
     * @throws IllegalArgumentException si la línea está vacía o compuesta
     *         únicamente por espacios en blanco
     */
    public static void validarLinea(String linea) {

        if (linea.isBlank()) {

            throw new IllegalArgumentException(
                    "La línea no puede estar vacía."
            );
        }
    }

    /**
     * Valida que una calificación se encuentre dentro del rango permitido.
     *
     * @param calificacion valor numérico que se desea validar
     * @throws CalificacionInvalidaException si el valor está fuera del
     *         rango 0-100
     */
    public static void validarCalificacion(int calificacion)
            throws CalificacionInvalidaException {

        if (calificacion < 0 || calificacion > 100) {

            throw new CalificacionInvalidaException(
                    "Calificación fuera de rango: " + calificacion
            );
        }
    }
}
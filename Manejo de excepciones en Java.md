*Nombre*: Mendivil Morales Erandeni
*Expediente*:213204857

## **1\. Objetivo de aprendizaje**

Al finalizar la práctica, el estudiante será capaz de **identificar, capturar, propagar y generar excepciones en Java**, aplicando buenas prácticas de manejo de errores mediante `try`, `catch`, `finally`, `throws`, `throw`, excepciones específicas y `try-with-resources`.

### **Resultados de aprendizaje**

El estudiante podrá:

* Diferenciar excepciones verificadas y no verificadas.  
* Utilizar correctamente `try`, `catch` y `finally`.  
* Propagar excepciones con `throws`.  
* Lanzar excepciones mediante `throw`.  
* Crear y utilizar una excepción personalizada.  
* Utilizar `try-with-resources`.  
* Aplicar criterios básicos de buenas prácticas en el manejo de excepciones.


# **2\. Situación de trabajo**

Se desarrollará una aplicación denominada **ProcesadorCalificaciones**, cuya función será leer un archivo de texto con calificaciones y calcular su promedio.

Archivo de ejemplo:

85  
90  
78  
abc  
95  
110  
70

Durante el procesamiento pueden ocurrir distintos problemas:

* el archivo no existe;  
* una línea no contiene un número;  
* una calificación está fuera del rango permitido;  
* ocurre un error de lectura.

La práctica consiste en hacer que la aplicación maneje estos problemas de manera controlada.

# **Parte I. Observar una excepción sin manejar**

## **3\. Crear el programa inicial**

Crear:

import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;

public class ProcesadorCalificaciones {

    public static void main(String\[\] args) throws IOException {

        BufferedReader lector \=  
            new BufferedReader(  
                new FileReader("calificaciones.txt")  
            );

        String linea;

        while ((linea \= lector.readLine()) \!= null) {  
            int calificacion \= Integer.parseInt(linea);  
            System.out.println(calificacion);  
        }

        lector.close();  
    }  
}

Ejecutar primero con un archivo válido y después con un archivo que contenga:

85  
90  
abc  
75

### **Actividad**

Registrar:

1. ¿Qué excepción aparece?
   java.io.FileNotFoundException
2. ¿En qué línea ocurre? 
    en la línea 10
3. ¿Continúa la ejecución? 
    No, termina con Process finished with exit code 1
4. ¿Qué información proporciona el *stack trace*?
    Dónde murió el programa, el tipo exacto de excepción java.io.FileNotFoundException, mensaje informativo que el sistema no encuentra el archivo especificado. 

El material explica que el objeto excepción contiene información sobre el tipo de error y el estado del programa cuando ocurrió, y que lanzar una excepción consiste en crear ese objeto y pasarlo al runtime.

# **Parte II. Capturar una excepción**

## **4\. Utilizar `try-catch`**

Modificar el procesamiento:

try {

    int calificacion \=  
        Integer.parseInt(linea);

    System.out.println(calificacion);

} catch (NumberFormatException e) {

    System.out.println(  
        "Valor inválido: " \+ linea  
    );  
}

Ejecutar nuevamente.

### **Observar**

Ahora el programa no termina cuando encuentra:

abc

sino que procesa las líneas siguientes.


# **5\. Actividad de aprendizaje 1: comparar comportamiento**

Ejecutar dos versiones:

### **Versión A**

Sin `try-catch`.

### **Versión B**

Con:

catch (NumberFormatException e)

Completar:

| Aspecto | Sin manejo | Con manejo |
| ----- | ----- | ----- |
| ¿Termina el programa? |Sí, abruptamente al encontrar abc; sale con código 1  | No, completa la lectura del archivo y termina normalmente (código 0) |
| ¿Se muestra el error? | 	Sí, pero como stack trace de NumberFormatException en la salida de error, sin contexto de negocio | Sí, con un mensaje controlado y legible: Valor inválido: abc |
| ¿Se procesan las líneas posteriores? | No; el 75 nunca se lee | Sí; el ciclo continúa y procesa 75 |
| ¿Puede recuperarse el programa? | No; la JVM interrumpe el hilo main | Sí; descarta la línea defectuosa y sigue con la siguiente iteración |

### **Reflexión**

¿Qué ventaja proporciona capturar una excepción que el programa puede anticipar?
Capturar una excepción anticipable convierte un fallo fatal en una condición de datos manejable. Que una línea traiga abc no es una falla del programa: es un dato sucio, algo perfectamente previsible en un archivo de calificaciones. Sin manejo, un solo carácter incorrecto invalida el procesamiento completo y las 200 líneas siguientes se pierden aunque fueran válidas.

Con el try-catch el programa distingue entre lo que puede resolver y lo que no. Aísla el error al ámbito donde ocurrió —una iteración— y conserva el trabajo ya hecho, lo que permite calcular el promedio con los datos aprovechables. Además el mensaje deja de ser un volcado técnico y pasa a ser información útil: se sabe qué línea se descartó y por qué, sin exponer detalles internos de la implementación.

# **Parte III. Múltiples excepciones**

## **6\. Capturar errores de archivo**

Ahora eliminar o cambiar el nombre de:

calificaciones.txt

La creación de:

new FileReader("calificaciones.txt")

puede generar una excepción de archivo no encontrado.

Modificar:

try {

    BufferedReader lector \=  
        new BufferedReader(  
            new FileReader("calificaciones.txt")  
        );

} catch (java.io.FileNotFoundException e) {

    System.err.println(  
        "No se encontró el archivo."  
    );  
}


# **7\. Capturar excepciones específicas primero**

Construir:

try {

    // procesamiento

} catch (NumberFormatException e) {

    System.err.println(  
        "Formato numérico inválido: "  
        \+ e.getMessage()  
    );

} catch (IllegalArgumentException e) {

    System.err.println(  
        "Argumento inválido: "  
        \+ e.getMessage()  
    );  
}


### **Pregunta**

¿Por qué sería incorrecto invertir el orden anterior?

Sería incorrecto porque NumberFormatException es una subclase de IllegalArgumentException, según la jerarquía Throwable → Exception → RuntimeException → IllegalArgumentException → NumberFormatException. Esto significa que toda NumberFormatException es también una IllegalArgumentException, pero no a la inversa.

Java evalúa los bloques catch en orden descendente y ejecuta el primero cuyo tipo sea compatible con la excepción lanzada. Si IllegalArgumentException se colocara primero, capturaría también los errores de formato numérico por ser un caso particular suyo, y el bloque de NumberFormatException quedaría como código inalcanzable. Se perdería así el mensaje específico y la capacidad de distinguir entre un dato no numérico y una calificación fuera de rango.

El compilador de Java impide esta situación: la compilación falla con el mensaje error: exception NumberFormatException has already been caught. No se trata de un error en tiempo de ejecución ni de una mala práctica de estilo, sino de un error que impide construir el programa.

Por esta razón, los bloques catch deben ordenarse siempre de la excepción más específica a la más general.
# **Parte IV. `finally`**

## **8\. Agregar limpieza de recursos**

Ejemplo:

BufferedReader lector \= null;

try {

    lector \=  
        new BufferedReader(  
            new FileReader("calificaciones.txt")  
        );

    // procesamiento

} catch (IOException e) {

    System.err.println(  
        "Error de entrada/salida: "  
        \+ e.getMessage()  
    );

} finally {

    if (lector \!= null) {  
        try {  
            lector.close();  
        } catch (IOException e) {  
            System.err.println(  
                "No fue posible cerrar el archivo."  
            );  
        }  
    }  
}



### **Actividad**

Probar:

* archivo correcto;  
* archivo inexistente;  
* contenido inválido.

¿En qué situaciones se ejecuta `finally`?
El bloque finally se ejecuta siempre que el flujo haya entrado al bloque try, sin importar la manera en que se salga de él. Es la garantía que ofrece el lenguaje para liberar recursos.
| Escenario | ¿Se ejecuta `finally`? | Comportamiento observado |
| --- | --- | --- |
| Archivo correcto | Sí | El `try` concluye sin errores; `lector` tiene un valor asignado y el archivo se cierra |
| Archivo inexistente | Sí | Falla la creación del `FileReader`, se atiende el `catch` y aun así `finally` se ejecuta; `lector` permanece en `null` |
| Contenido inválido | Sí | La excepción se maneja dentro del ciclo, el `try` externo termina normalmente y el archivo se cierra |

# **Parte V. `try-with-resources`**

## **9\. Simplificar el cierre de recursos**

El material recomienda también `try-with-resources` para objetos que implementan `AutoCloseable`; el recurso se cierra automáticamente al finalizar el bloque.

Reescribir:

try (  
    BufferedReader lector \=  
        new BufferedReader(  
            new FileReader("calificaciones.txt")  
        )  
) {

    String linea;

    while ((linea \= lector.readLine()) \!= null) {

        System.out.println(linea);  
    }

} catch (IOException e) {

    System.err.println(  
        "Error al leer el archivo: "  
        \+ e.getMessage()  
    );  
}

### **Comparación**

Responder:

* ¿Qué código desapareció?  
  Desapareció todo el bloque finally, que en la versión anterior ocupaba alrededor de diez líneas: la comprobación if (lector != null), la llamada a lector.close() y el try-catch anidado que la envolvía para atender la posible IOException del cierre. También desapareció la declaración previa BufferedReader lector = null;, ya que la variable ahora se declara dentro de los paréntesis del try. En conjunto, se eliminó el código dedicado a la gestión del recurso y permaneció únicamente el que resuelve el problema.
* ¿Quién cierra ahora el archivo?  
  Lo cierra la máquina virtual de Java de manera automática. Cualquier recurso declarado en los paréntesis del try debe implementar la interfaz AutoCloseable; BufferedReader la implementa, por lo que el compilador genera el llamado a close() y lo ejecuta al salir del bloque, ya sea por terminación normal o por una excepción. El cierre ocurre incluso antes de que se evalúen los bloques catch. Además, si tanto el procesamiento como el cierre fallan, la excepción del cierre se registra como suppressed y se conserva la original, en lugar de perderse como sucedía con el enfoque manual.
* ¿Qué versión resulta más clara?
  La versión con try-with-resources. La diferencia no es solamente de extensión: el finally manual exige recordar tres detalles fáciles de omitir —declarar la variable fuera del try, verificar que no sea null y capturar la excepción que puede lanzar el propio close()—. Cada uno de ellos es una fuente potencial de errores, y olvidar el cierre provoca fugas de recursos que no se manifiestan de inmediato.

---

# **Parte VI. Lanzar excepciones con `throw`**

## **10\. Validar calificaciones**

Crear:

public static void validarCalificacion(  
        int calificacion) {

    if (calificacion \< 0 ||  
        calificacion \> 100\) {

        throw new IllegalArgumentException(  
            "La calificación debe estar entre 0 y 100: "  
            \+ calificacion  
        );  
    }  
}

Utilizar:

int calificacion \=  
    Integer.parseInt(linea);

validarCalificacion(calificacion);

El material señala que `throw` requiere un objeto `Throwable` y se utiliza para lanzar explícitamente una excepción.

# **11\. Actividad de aprendizaje 2: diseñar validaciones**

Agregar validaciones para detectar:

* línea vacía;  
* número negativo;  
* número mayor de 100\.

Ejemplo:

if (linea.isBlank()) {  
    throw new IllegalArgumentException(  
        "La línea no puede estar vacía."  
    );  
}

Cada equipo deberá definir:

| Condición | Excepción | Mensaje |
| ----- | ----- | ----- |
| Línea vacía |  |  |
| Valor no numérico |  |  |
| Valor \< 0 |  |  |
| Valor \> 100 |  |  |

El material recomienda utilizar excepciones lo más específicas posible y evitar declarar genéricamente `throws Exception`.

# **Parte VII. Propagar excepciones con `throws`**

## **12\. Crear método de lectura**

Separar responsabilidades:

public static void procesarArchivo(  
        String nombreArchivo)  
        throws IOException {

    try (  
        BufferedReader lector \=  
            new BufferedReader(  
                new FileReader(nombreArchivo)  
            )  
    ) {

        String linea;

        while ((linea \= lector.readLine()) \!= null) {  
            System.out.println(linea);  
        }  
    }  
}

Y en `main`:

try {

    procesarArchivo(  
        "calificaciones.txt"  
    );

} catch (IOException e) {

    System.err.println(  
        "No fue posible procesar el archivo."  
    );  
}

El material indica que, en ciertos casos, es preferible que un método superior en la pila de llamadas maneje la excepción; en tal situación, el método que puede generarla la especifica mediante `throws`.


# **Parte VIII. Crear una excepción personalizada**

## **13\. Definir `CalificacionInvalidaException`**

El material señala que pueden crearse clases propias de excepción cuando sea necesario representar problemas específicos de la aplicación.

Crear:

public class CalificacionInvalidaException  
        extends Exception {

    public CalificacionInvalidaException(  
            String mensaje) {

        super(mensaje);  
    }  
}

Modificar:

public static void validarCalificacion(  
        int calificacion)  
        throws CalificacionInvalidaException {

    if (calificacion \< 0 ||  
        calificacion \> 100\) {

        throw new CalificacionInvalidaException(  
            "Calificación fuera de rango: "  
            \+ calificacion  
        );  
    }  
}

Capturar:

catch (CalificacionInvalidaException e) {

    System.err.println(  
        "Error en los datos: "  
        \+ e.getMessage()  
    );  
}  
---

# **Parte IX. Actividad integradora**

## **14\. Construir un procesador robusto**

La aplicación deberá leer:

85  
90  
abc  
78  
110  
72  
\-5  
95

Y comportarse aproximadamente así:

Calificación válida: 85  
Calificación válida: 90  
Dato ignorado: abc  
Calificación válida: 78  
Calificación fuera de rango: 110  
Calificación válida: 72  
Calificación fuera de rango: \-5  
Calificación válida: 95

Promedio de valores válidos: 84.00

### **Requisitos**

La solución deberá utilizar:

1. `try`.  
2. Al menos dos bloques `catch`.  
3. `try-with-resources`.  
4. `throws`.  
5. `throw`.  
6. `NumberFormatException`.  
7. `IOException`.  
8. Una excepción personalizada.  
9. Mensajes descriptivos.

---

# **15\. Código base sugerido**

import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;

public class ProcesadorCalificaciones {

    public static void main(String\[\] args) {

        try {

            double promedio \=  
                calcularPromedio(  
                    "calificaciones.txt"  
                );

            System.out.printf(  
                "Promedio: %.2f%n",  
                promedio  
            );

        } catch (IOException e) {

            System.err.println(  
                "No fue posible procesar el archivo: "  
                \+ e.getMessage()  
            );  
        }  
    }

    public static double calcularPromedio(  
            String archivo)  
            throws IOException {

        double suma \= 0;  
        int contador \= 0;

        try (  
            BufferedReader lector \=  
                new BufferedReader(  
                    new FileReader(archivo)  
                )  
        ) {

            String linea;

            while ((linea \= lector.readLine()) \!= null) {

                try {

                    int calificacion \=  
                        Integer.parseInt(  
                            linea.trim()  
                        );

                    validarCalificacion(  
                        calificacion  
                    );

                    suma \+= calificacion;  
                    contador++;

                } catch (NumberFormatException e) {

                    System.err.println(  
                        "Dato no numérico: "  
                        \+ linea  
                    );

                } catch (  
                    CalificacionInvalidaException e  
                ) {

                    System.err.println(  
                        e.getMessage()  
                    );  
                }  
            }  
        }

        return suma / contador;  
    }

    public static void validarCalificacion(  
            int calificacion)  
            throws CalificacionInvalidaException {

        if (calificacion \< 0 ||  
            calificacion \> 100\) {

            throw new CalificacionInvalidaException(  
                "Calificación fuera de rango: "  
                \+ calificacion  
            );  
        }  
    }  
}  
---

# **Parte X. Aplicar buenas prácticas**

## **16\. Actividad de aprendizaje 3: Code Review**

Analizar el siguiente código:

try {

    int numero \=  
        Integer.parseInt(valor);

} catch (Throwable e) {

}

Identificar al menos tres problemas.

### **Aspectos esperados**

* Captura `Throwable`.  
* Ignora completamente la excepción.  
* No utiliza una excepción específica.  
* No proporciona información del error.

Proponer una versión mejorada.


# **17\. Documentar excepciones**

El material recomienda documentar mediante `@throws` las excepciones especificadas por un método.

Ejemplo:

/\*\*  
 \* Valida una calificación.  
 \*  
 \* @param calificacion valor a validar  
 \* @throws CalificacionInvalidaException  
 \*         si el valor está fuera del rango 0-100  
 \*/  
public static void validarCalificacion(  
        int calificacion)  
        throws CalificacionInvalidaException {  
    // ...  
}

### **Actividad**

Agregar Javadoc a:

* `validarCalificacion()`;  
* `calcularPromedio()`.

# **18\. Buenas prácticas a verificar**

Antes de entregar, revisar:

| Buena práctica | Cumple | Evidencia en la solución |
| --- | --- | --- |
| Utiliza excepciones específicas | ☑ | Se emplean `NumberFormatException`, `FileNotFoundException`, `IllegalArgumentException` y `CalificacionInvalidaException`, cada una para una condición distinta |
| No captura `Throwable` | ☑ | Ningún `catch` declara `Throwable` ni `Error`; la excepción más general capturada es `IOException` |
| No ignora excepciones | ☑ | Todos los bloques `catch` emiten un mensaje; ninguno queda vacío |
| Usa mensajes descriptivos | ☑ | Los mensajes indican la causa y el dato que la provocó, por ejemplo "Calificación fuera de rango: 110" |
| Captura primero excepciones específicas | ☑ | `FileNotFoundException` precede a `IOException`, y `NumberFormatException` precede a `IllegalArgumentException` |
| Utiliza `try-with-resources` | ☑ | El `BufferedReader` se declara en los paréntesis del `try` dentro de `calcularPromedio()` |
| Documenta `throws` | ☑ | `calcularPromedio()` y `validarCalificacion()` incluyen la etiqueta `@throws` en su Javadoc |
| No utiliza `throws Exception` sin necesidad | ☑ | Las firmas declaran únicamente `IOException` y `CalificacionInvalidaException` |

El material también advierte contra registrar una excepción y volver a lanzarla innecesariamente, pues puede producir múltiples mensajes para el mismo problema; cuando se requiere agregar contexto, propone envolver la excepción preservando la causa original.

# **19\. Reto final**

Modificar la aplicación para recibir el nombre del archivo por argumento:

java ProcesadorCalificaciones calificaciones.txt

Si no se proporciona el argumento:

Uso:  
java ProcesadorCalificaciones \<archivo\>

El programa deberá manejar adecuadamente:

* archivo inexistente;  
* línea vacía;  
* texto no numérico;  
* calificación fuera de rango;  
* error de lectura;  
* archivo sin ninguna calificación válida.

Para el último caso, diseñar una excepción:

SinDatosValidosException  
---

# **20\. Entregables**

Cada estudiante entregar:

* Liga a repositorio
* `ProcesadorCalificaciones.java`  
* `CalificacionInvalidaException.java`  
* `SinDatosValidosException.java`, si se realiza el reto  
* `calificaciones.txt`  
* evidencia de las ejecuciones;  
* respuestas a las preguntas de reflexión;  
* breve explicación de qué excepciones son *checked* y cuáles *unchecked* dentro de su solución.

# **21\. Preguntas de reflexión**

1. ¿Qué diferencia existe entre lanzar y capturar una excepción?

Lanzar consiste en crear un objeto de excepción y entregarlo al runtime para señalar que ocurrió una condición anómala; interrumpe el flujo normal y transfiere el control hacia arriba en la pila de llamadas. Capturar es interceptar esa excepción para decidir qué hacer con ella. Son los dos extremos del mecanismo: quien lanza detecta el problema, quien captura lo resuelve.

2. ¿Qué función tiene try?

Delimita el bloque de código que se vigila. Todo lo que se ejecuta dentro queda bajo observación, de modo que si se produce una excepción, el flujo se desvía hacia los bloques catch asociados en lugar de terminar el programa.

3. ¿Qué función tiene catch?

Define el tratamiento para un tipo específico de excepción. Recibe el objeto lanzado como parámetro, lo que permite consultar su mensaje y su origen, y contiene el código de recuperación: informar al usuario, registrar el error, asignar un valor por defecto o descartar el dato y continuar.

4. ¿Cuándo resulta útil finally?

Cuando existe código que debe ejecutarse sin importar el resultado del try, típicamente la liberación de recursos: cerrar archivos, conexiones de red o de base de datos. Se ejecuta tanto si el bloque termina con éxito como si se produjo una excepción, e incluso cuando hay un return de por medio.

5. ¿Qué ventaja tiene try-with-resources?

Cierra automáticamente los recursos que implementan AutoCloseable al salir del bloque, eliminando la necesidad del finally manual con su comprobación de nulos y su try-catch anidado. Reduce el código, evita las fugas de recursos por olvido y conserva la excepción original cuando el cierre también falla, registrando esta última como suppressed.

6. ¿Cuál es la diferencia entre throw y throws?

throw es una instrucción que lanza efectivamente una excepción en un punto concreto del código; requiere un objeto Throwable. throws es una cláusula en la firma del método que declara qué excepciones puede propagar hacia quien lo invoca. Uno ejecuta la acción, el otro anuncia la posibilidad.

7. ¿Por qué conviene utilizar excepciones específicas?

Porque permiten distinguir qué falló exactamente y responder de forma distinta a cada situación. En esta solución, un dato no numérico y una calificación fuera de rango son problemas diferentes que merecen mensajes diferentes. Capturar un tipo genérico borra esa distinción y, además, atrapa errores no previstos que deberían propagarse.

8. ¿Cuándo tiene sentido crear una excepción personalizada?

Cuando el problema pertenece al dominio de la aplicación y ninguna excepción estándar lo representa con precisión. CalificacionInvalidaException expresa una regla del negocio —el rango 0 a 100— que ninguna clase de la biblioteca estándar conoce. Además, contar con un tipo propio permite capturarla de forma selectiva sin confundirla con otros errores.

9. ¿Por qué no se recomienda capturar Throwable?

Porque es la raíz de toda la jerarquía e incluye la rama Error, con fallos de la máquina virtual como OutOfMemoryError o StackOverflowError, de los que una aplicación no puede recuperarse. Capturarlos deja al programa operando sobre una JVM en estado comprometido y oculta el problema real, que reaparecerá después en un lugar sin relación aparente con su origen.

10. ¿Qué efecto tiene ignorar una excepción?

El error desaparece sin dejar rastro y el programa continúa como si nada hubiera ocurrido, produciendo resultados incorrectos de forma silenciosa. Es peor que un fallo visible, porque este último al menos indica dónde y cuándo se produjo. Un catch vacío convierte un problema diagnosticable en uno invisible.
11. ¿Qué información debería proporcionar un buen mensaje de excepción?

Qué ocurrió, con qué dato concreto y, cuando sea posible, qué se esperaba. El mensaje "Calificación fuera de rango: 110" cumple los tres criterios: identifica el problema, incluye el valor rechazado e implica la regla incumplida. Debe ser comprensible para quien lo lea sin acceso al código, y no exponer detalles internos de la implementación.
12. ¿En qué casos conviene propagar una excepción en lugar de capturarla inmediatamente?
Cuando el método donde ocurre no dispone de la información necesaria para decidir qué hacer. calcularPromedio() sabe leer archivos, pero no si ante uno inexistente conviene mostrar un mensaje, solicitar otra ruta o terminar el programa: esa decisión corresponde al nivel superior. En cambio, los errores de validación sí se capturan dentro del ciclo, porque ahí sí se sabe la respuesta adecuada, que es descartar la línea y continuar.
---

# **22\. Criterios de evaluación**

| Criterio | Ponderación |
| ----- | ----- |
| Identificación correcta de situaciones excepcionales | 10% |
| Uso correcto de `try-catch` | 20% |
| Uso de excepciones específicas | 15% |
| Uso correcto de `throw` y `throws` | 15% |
| Implementación de excepción personalizada | 10% |
| Uso de `try-with-resources` | 10% |
| Aplicación de buenas prácticas | 10% |
| Calidad y claridad del código | 5% |
| Reflexión y evidencias | 5% |
| **Total** | **100%** |



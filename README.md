# Tarea: Taller mecánico
## Profesor: Andrés Rubio del Río
## Alumno: Manuel Martínez Alcalá

Nuestro cliente está tan encantado con todo el trabajo realizado en las fases anteriores del proyecto del taller mecánico. Sin embargo, quiere dar un paso más en dicho proyecto para lo cual que nos pide que añadamos persistencia a los datos pero sin necesidad de tener una base de datos. Aunque es consciente que esa es la mejor opción, nos pide que en este spring se añada persistencia a los datos pero utilizando ficheros JSON.

Al analizar cómo llevaremos a cabo la persistencia, se pide que los datos deben estar actualizados en los ficheros correspondientes en tiempo real. Para ello, al proyecto se añadirá un nuevo paquete ficheros con todo lo necesario para lograr dicha persistencia.

En el repositorio puedes encontrar un proyecto gradle con las dependencias necesarias del proyecto y con el punto de partida para este spring.

Para abordar dicho spring te muestro el diagrama de clases (en el que cuando se expresa cardinalidad * queremos expresar que se hará uso de listas) para el mismo y poco a poco te iré explicando los diferentes pasos a realizar:

![Diagrama de clases de la tarea](src/main/resources/uml/tallerMecanico.png)


#### Primeros Pasos
1. Modifica el archivo `README.md` para que incluya tu nombre en el apartado "Alumno".
2. Realiza tu primer commit.

#### Paquete dominio
1. Modifica la clase `Trabajo` para que los atributos `LocalDate` puedan ser guardados y leídos correctamente cuando se trabaja con ficheros JSON. Para ello deberás usar la notación `@JsonFormat(pattern = "yyyy-MM-dd")`.
2. Modifica la clase `Trabajo` para que automatice la serialización/deserialización de los objetos de tipo **Mecánico** o de tipo **Revisión**. Para ello deberás usar las anotaciones mostradas a continuación. Mediante estas anotaciones se indica en una pareja clave-valor, cual es el tipo de trabajo.
   `@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   include = JsonTypeInfo.As.PROPERTY,
   property = "XXXX"
   )`
   debiendo reemplazar XXXX por el nombre que se le quiera dar a la clave.
   
   `@JsonSubTypes({
   @JsonSubTypes.Type(value = YYYYY.class, name = "AAAAA"),
   @JsonSubTypes.Type(value = ZZZZZ.class, name = "BBBBB")
   })`
   debiendo reemplazar **YYYYY** y **ZZZZZ** por las clases hijas, y **AAAAA** y **BBBBB** por el valor que tendrá la clave **XXXXX**.
3. Modifica la clase `Trabajo` para que los métodos abstractos o aquellos que se vean afectados por hacer uso de los mismos, sean marcados como **Ignore**. Para ello debes usar la notación `@JsonIgnore`.
4. Realiza un **commit**

#### Paquete ficheros
1. Crea un nuevo paquete en la capa `negocio` llamado `json`.
2. Crea la clase `FuenteDatosFicherosJSON` que deberá implementar la interfaz `IFuenteDatos`, tal y como se indica en el diagrama. Esta clase será la encargada de implementar el **patrón fábrica**, devolviendo en cada caso el resultado de crear la clase que hace referencia a su nombre:
   2.1. Método `crearClientes` devolverá una instancia de tipo `Clientes` del paquete `ficheros`.
   2.2. Método `crearVehiculos` devolverá una instancia de tipo Vehiculos del paquete `ficheros`.
   2.3. Método `crearTrabajos` devolverá una instancia de tipo Trabajos del paquete `ficheros`.
   2.4. Realiza un **commit**.
3. Crea la clase `Clientes`, que tendrá en el atributo `FICHERO_CLIENTES` la ruta donde se encuentra el fichero donde leer y escribir los datos de los clientes. Dicha ruta, situada en la carpeta raíz del proyecto será `datos/ficheros/json/clientes.json` y su estructura será la siguiente:
    ~~~json
   [ {
      "nombre" : "Bob Esponja",
      "dni" : "11223344B",
      "telefono" : "950112233"
      }, {
      "nombre" : "Patricio Estrella",
      "dni" : "11111111H",
      "telefono" : "950111111"
      }, {
      "nombre" : "Chase",
      "dni" : "75723203G",
      "telefono" : "950121212"
      } ]
   ~~~

      Por otro lado, para el correcto funcionamiento de la clase, crea los atributos y añade los métodos indicados a continuación:

      1. Crea el atributo `instancia` para aplicar el **patrón singleton**.
      2. Crea el atributo `mapper` para mapear los datos existentes en el fichero `clientes.json`.
      3. El método `getInstancia` para aplicar el **patrón singleton** a la clase.
      4. El Método `comenzar`que deberá estar vacío ya que los datos se leerán cuando los solicite el usuario.
      5. El Método `terminar` que deberá estar vacío ya que los datos se escribirán cada vez que haya alguna modificación.
      6. El Método `leer` que deberá leer cada uno de los clientes almacenados en el fichero JSON, devolviendo una lista formada por dichos clientes.
      7. El Método `escribir` que escribe en el fichero JSON cada uno de los clientes existentes en la lista recibida como parámetro.
      8. El Método `get` que devuelve una lista de los clientes existentes en el fichero JSON correspondiente.
      9. El Método `insertar` que deberá añadir un nuevo cliente al fichero JSON correspondiente.
      10. El Método `modificar` que actualizará el nombre y/o teléfono del cliente recibido como parámetro en el fichero JSON correspondiente.
      11. El Método `buscar` que deberá devolver el resultado de encontrar en el fichero JSON correspondiente al cliente pasado como parámetro.
      12. El Método `borrar` que deberá eliminar del fichero JSON correspondiente al cliente pasado como parámetro.
      13. Realiza un **commit**.
   

4. Crea la clase `Vehiculos`, que tendrá en el atributo `FICHERO_VEHICULOS` la ruta donde se encuentra el fichero donde leer y escribir los datos de los vehículos. Dicha ruta, situada en la carpeta raíz del proyecto será `datos/ficheros/json/vehiculos.json` y su estructura será la siguiente:
    ~~~json
      [ {
      "marca" : "Scania",
      "modelo" : "Citywide",
      "matricula" : "1234BCD"
      }, {
      "marca" : "Seat",
      "modelo" : "León",
      "matricula" : "1111BBB"
      }, {
      "marca" : "Renault",
      "modelo" : "Megane",
      "matricula" : "2222CCC"
      }, {
      "marca" : "Mercedes-Benz",
      "modelo" : "eSprinter",
      "matricula" : "3333DDD"
      }, {
      "marca" : "Seat",
      "modelo" : "León",
      "matricula" : "9999ZZZ"
      } ]
   ~~~
   Por otro lado, para el correcto funcionamiento de la clase, crea los atributos y añade los métodos indicados a continuación:

   1. Crea el atributo `instancia` para aplicar el **patrón singleton**.
   2. Crea el atributo `mapper` para mapear los datos existentes en el fichero `vehiculos.json`.
   3. El método `getInstancia` para aplicar el **patrón singleton** a la clase.
   4. El Método `comenzar`que deberá estar vacío ya que los datos se leerán cuando los solicite el usuario.
   5. El Método `terminar` que deberá estar vacío ya que los datos se escribirán cada vez que haya alguna modificación.
   6. El Método `leer` que deberá leer cada uno de los vehículos almacenados en el fichero JSON, devolviendo una lista formada por dichos vehículos.
   7. El Método `escribir` que escribe en el fichero JSON cada uno de los vehículos existentes en la lista recibida como parámetro.
   8. El Método `get` que devuelve una lista de los vehículos existentes en el fichero JSON correspondiente.
   9. El Método `insertar` que deberá añadir un nuevo vehículo al fichero JSON correspondiente.
   10. El Método `buscar` que deberá devolver el resultado de encontrar en el fichero JSON correspondiente al vehículo pasado como parámetro.
   11. El Método `borrar` que deberá eliminar del fichero JSON correspondiente al vehículo pasado como parámetro.
   12. Realiza un **commit**.
   

5. Crea la clase `Trabajos`, que tendrá en el atributo `FICHERO_TRABAJOS` la ruta donde se encuentra el fichero donde leer y escribir los datos de los trabajos. Dicha ruta, situada en la carpeta raíz del proyecto será `datos/ficheros/json/trabajos.json` y su estructura será la siguiente:
~~~json
   [ {
   "tipo" : "Revision",
   "cliente" : {
   "nombre" : "Patricio Estrella",
   "dni" : "11111111H",
   "telefono" : "950111111"
   },
   "vehiculo" : {
   "marca" : "Seat",
   "modelo" : "León",
   "matricula" : "1111BBB"
   },
   "fechaInicio" : "2025-11-01",
   "horas" : 24
   }, {
   "tipo" : "Mecanico",
   "cliente" : {
   "nombre" : "Chase",
   "dni" : "75723203G",
   "telefono" : "950121212"
   },
   "vehiculo" : {
   "marca" : "Renault",
   "modelo" : "Megane",
   "matricula" : "2222CCC"
   },
   "fechaInicio" : "2025-10-30",
   "fechaFin" : "2025-11-02",
   "horas" : 11,
   "precioMaterial" : 12.0
   }, {
   "tipo" : "Mecanico",
   "cliente" : {
   "nombre" : "Chase",
   "dni" : "75723203G",
   "telefono" : "950121212"
   },
   "vehiculo" : {
   "marca" : "Mercedes-Benz",
   "modelo" : "eSprinter",
   "matricula" : "3333DDD"
   },
   "fechaInicio" : "2025-11-02"
   } ]
~~~
   Por otro lado, para el correcto funcionamiento de la clase, crea los atributos y añade los métodos indicados a continuación:
   1. Crea el atributo `instancia` para aplicar el **patrón singleton**.
   2. Crea el atributo `mapper` para mapear los datos existentes en el fichero `trabajos.json`.
   3. El método `getInstancia` para aplicar el **patrón singleton** a la clase.
   4. El Método `comenzar`que deberá estar vacío ya que los datos se leerán cuando los solicite el usuario.
   5. El Método `terminar` que deberá estar vacío ya que los datos se escribirán cada vez que haya alguna modificación.
   6. El Método `leer` que deberá leer cada uno de los trabajos almacenados en el fichero JSON, devolviendo una lista formada por dichos trabajos.
   7. El Método `escribir` que escribe en el fichero JSON cada uno de los trabajos existentes en la lista recibida como parámetro.
   8. El Método `get` que devuelve una lista de los trabajos existentes en el fichero JSON correspondiente.
   9. El Método `insertar` que deberá añadir un nuevo trabajo al fichero JSON correspondiente.
   10. El Método `buscar` que deberá devolver el resultado de encontrar en el fichero JSON correspondiente al trabajo pasado como parámetro.
   11. El Método `borrar` que deberá eliminar del fichero JSON correspondiente al trabajo pasado como parámetro.
   12. Realiza un **commit**.
#### Paquete modelo
1. Actualiza la clase `FactoriaFuenteDatos` para que contemple la opción de ficheros JSON. Este enumerado implementa el **patrón método de fabricación** para la fuente de datos que se va a tener: `FICHEROS_JSON`.
2. Realiza un **commit**.

#### Main
1. Modifica el método `procesarArgumentosFuenteDatos` que creará un modelo cuya fuente de datos será la que se indique a través de los parámetros de la aplicación. Si el parámetro es `-fdficherosjson`, se creará un modelo cuya fuente de datos será de tipo `FICHEROS_JSON`. En caso de no indicar ninguna fuente de datos, por defecto, se considerará que se operará sobre ficheros JSON.
2. Realiza el **commit** correspondiente.
3. Finalmente, realiza el **push** hacia tu repositorio remoto en GitHub.

#### Se valorará:

- La indentación debe ser correcta en cada uno de los apartados.
- Los identificadores utilizados deben ser adecuados y descriptivos.
- Se debe utilizar la clase `Entrada` para realizar la entrada por teclado que se encuentra como dependencia de nuestro proyecto en la librería entrada.
- El programa debe pasar todas las pruebas que van en el esqueleto del proyecto y toda entrada del programa será validada, para evitar que el programa termine abruptamente debido a una excepción.
- La corrección ortográfica tanto en los comentarios como en los mensajes que se muestren al usuario.



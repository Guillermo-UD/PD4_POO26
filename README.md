# STANDS FILEY UADY (MVC)


Sistema desarrdo bajo el patron de arquitectura Modelo-Vista-Controlador (MVC), dise{ado para la gestion de stands. El proyecto se conecta a una base de datos local en MySQL y gestiona sus dependencias mediante Maven.


cambios realizados (Separacion de las conexiones y los querys en clases y archivos distintos)
## Estructura del Repositorio

El codigo fuente principal se encuentra organizado de la siguiente manera:

- [`src/main/java/filey/`](src/main/java/filey)
  - [`controlador/`](src/main/java/filey/controlador) - Modulo encargado de la logica de control y la comunicacion entre la vista y el modelo.
  - [`modelo/`](src/main/java/filey/modelo) - Modulo que contiene la gestion de datos, conexion a MySQL y las consultas SQL.
  - [`Main.java`](src/main/java/filey/Main.java) - Clase principal que inicializa y ejecuta la aplicacion.olla

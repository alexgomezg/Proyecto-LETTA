# CONTRIBUTING

1.  Empezando
2.  Desarrollo de una tarea
3.  Estructura del proyecto
4.  Entorno de desarrollo
5.  Configuración de un entorno de desarrollo
6.  Control de versiones (Git)
7.  Tests
	7.1.   Tests por módulo
8.  Guía de estilo



## 1. Empezando

El proyecto Letta se desarrolla en un entorno de integración continua con despliegue continuo en un servidor de pre-producción (staging). Este entorno está compuesto por varias herramientas que automatizan el proceso, todas ellas dirigidas por el POM de este proyecto.

En este documento encontrarás una descripción de este entorno y las instrucciones para saber cómo contribuir correctamente a este proyecto.



## 2. Desarrollo de una tarea

El proceso que se seguirá para realizar una tarea será:

 1. En Kunagi se seleccionará la tarea de la que el miembro del equipo sea responsable.
 2. Abre el entorno de desarrollo.
 3. Verificar que te encuentras en la rama develop.
 4. Haz pull de los últimos cambios.
 5. Implementa la solución, incluyendo los tests.
 6. Haz un commit con cada parte estable que desarrolles.
 7. Cada vez que hagas un commit se subirá a develop para compartirlo con el resto del equipo.
 8. Cuando acabes la jornada de trabajo recuerda introducir las horas en la tarea de Kunagi.


## 3. Estructura del proyecto

El proyecto está estructurado en 6 capas:
- **tests:** capa que contiene utilidades para realizar los tests. Ésta será importada por el resto de capas con el scope test para hacer uso de sus utilidades.
- **domain:** capa que contiene las clases de dominio (entidades).
- **dao:** capa que contiene las clases que se comunican con la base de datos.
- **rest:** capa que contiene los servicios REST.
- **html SPA:** capa que contiene una única página web la cual se actualiza mediante ficheros JavaScript.

## 4. Entorno de desarrollo

   - **Maven 3:** es un entorno de construcción de proyectos para Java. Esta será una herramienta clave, ya que es quien dirigirá todo el proyecto. Es necesario que tengas instalado Maven 3 en tu equipo de desarrollo para poder construir el proyecto. 
   - **Kunagi:** es una herramienta de gestión de proyectos Scrum. En ella encontrarás toda la información sobre las funcionalidades desarrolladas y por desarrollar, el alcance de las publicaciones, el estado de desarrollo, etc. Puedes acceder a través del siguiente enlace. 
   - **Git y Gitlab:** Git es el sistema de control de versiones que se utiliza en el proyecto. Es un sistema de control de versiones distribuido que facilita la colaboración entre desarrolladores. Es necesario que tengas instalado Git en tu sistema para poder realizar cambios en el proyecto y colaborar con el resto del equipo. Por otro lado, Gitlab es un front-end del repositorio Git común. Esta herramienta facilita la visualización de los commits y ficheros del proyecto, además de   	proporcionar alguna otra funcionalidad que mejora la colaboración. Puedes acceder a través del siguiente enlace.
   - **MySQL 5+:** es el sistema gestor de base de datos (SGDB) que utilizará el sistema definitivo. En la explicación de cómo ejecutar el sistema en local utilizaremos este SGBD, por lo que deberás tenerlo instalado en tu equipo.

## 5. Configuración de un entorno de desarrollo

Empezar a trabajar en el proyecto es tan sencillo como seguir los siguientes pasos: 
1. **Instala Git y Maven**. Si estás en un entorno Ubuntu es tan sencillo como ejecutar sudo apt-get install git maven. También es recomendable que instales algún visor de Git como gitk o qgit.
2. **Clona el repositorio Git** utilizando el comando: git clone http://singgroup.org/dt/gitlab/daa2021-teamA/letta.git
3. **Instala Eclipse for Java EE** (opcional pero recomendado):
	1. Descarga el IDE desde https://www.eclipse.org/downloads/eclipsepackages/
	2. Importa el proyecto en Eclipse utilizando Import...->Existing Maven projects, selecciona el directorio del proyecto en Root directory y marca todos los proyectos que aparezcan.

## 6. Control de versiones (Git)

El modelo de control de versiones que utilizaremos inicialmente será muy sencillo ya que solo utilizaremos dos ramas:

- **master:** a esta rama solo se enviarán los commits cuando se llegue a una versión estable y publicable (una release). Estas versiones deberán estar etiquetadas con nombre de la tarea realizada (funcionalidad).

- **develop:** esta será la rama principal de trabajo. Los commits que se envíen deben ser estables.

- **nombre_integrante:** cada miembro del equipo puede tener una rama con su nombre, en la que podrá subir código que haya estado desarrollando pero que aún no sea funcional. Debe tenerse en cuenta que estas ramas son auxiliarse y debe intentarse, en la medida de lo posible, mantener el trabajo actualizado subiéndolo a la rama develop.

## 7. Tests

Lo primero que se debe tener en cuenta a la hora de realizar tests es la existencia de la capa tests. Este proyecto está pensado para recoger las clases de utilidad que puedan ser compartidas por los tests de las distintas capas que forman el proyecto. Por lo tanto, siempre que exista una clase o fichero que sea compartido por varios proyectos, debería almacenarse en esta capa.

En segundo lugar, es importante ser consciente de que, dependiendo de la capa en la que nos encontremos, deberemos hacer diferentes tipos de test.

Por último, como norma general, los métodos de prueba deben ser lo más sencillos posible, de modo que sea sencillo comprender qué es lo que se está evaluando. En base a esta regla, no añadiremos documentación Javadoc a los métodos de prueba.

### 7.1 Tests por capa

Los tests que se deben hacer varían según la capa en la que nos encontremos. En concreto, los tests que habrá que hacer serán los siguientes:

- **domain:** tests de unidad para probar las entidades. Solo se testearán los 
constructores y los métodos con una cierta lógica, como pueden ser los métodos 
de las relaciones.

- **rest:** tests de integración REST Client.


## 8. Guía de estilo

Un elemento importante para poder colaborar es que exista una uniformidad en el código y otros elementos que forman parte del desarrollo. Esta sección sirve como una pequeña guía de estilo que debe respetarse al trabajar en el proyecto.

### 8.1. Código fuente

Para uniformizar el código fuente deben respetarse las siguientes normas:

- **Idioma:** todo el código debe desarrollarse en inglés, documentación en castellano.
- **Formato de código:** el código debe estar formateado, preferiblemente, siguiendo la Guía de Estilo para Java de Google o, al menos, utilizando el formato de código de Eclipse (Ctrl+Mayus+F).
- **Comentarios:** debe evitarse completamente el código comentado y, en la medida de lo posible, los comentarios en el código. 

### 8.2. Control de versiones

Una de las bases de desarrollo que utilizaremos en este proyecto es el integrar tan pronto como se pueda. Para ello, deben seguirse las siguientes normas: 
- **Contenido de los commits:** los commits, en la rama develop, deben ser completos en el sentido de que no deben romper la construcción. Además, el código debe estar probado, incluyendo los tests descritos en la sección 7, para que el resto de desarrolladores puedan confiar en el código.
- **Formato:** el formato de los commits deberá respetar las siguientes normas:
	- Escritos en inglés. 
	- Limitar el tamaño de línea a 80 columnas. Si se utiliza Eclipse, esto se hace de forma automática.
	- Primera línea descriptiva de lo que hace el commit: 
		- Si está relacionado con alguna tarea concreta de las descritas en Kunagi, debe comenzar con el identificador de la tarea (p.ej. "tsk1 Adds..."). 
		- Si está relacionado con varias tareas, se detallarán todas según lo indicado en el punto anterior separadas por " ; ".
		- Debe estar redactada en tercera persona del presente (p.ej. Adds..., Improves..., Modifies..., etc.). 
		- No debe llevar punto al final. 
	- Frecuencia de commit: los commits deben hacerse en pequeños pasos para que la frecuencia sea alta. Para ello es recomendable desarrollar de 	una forma ordenada, atacando partes concretas.
	- Frecuencia de push: siempre que se haga un commit debe hacerse un push. La única excepción a esta regla es que estemos haciendo pruebas locales para evaluar una posible solución. En tal caso, es recomendable que esto se haga en una rama personal para evitar enviar commits accidentalmente a la rama develop remota.
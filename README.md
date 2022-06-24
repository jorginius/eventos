# Eventos

Aplicación Spring Boot y Vaadin que muestra una tabla con eventos. Los eventos se generan de forma aleatoria en una base
de datos empotrada en memoria ([H2](https://www.h2database.com)).

El proyecto usa Maven y se incluye el wrapper. La meta por defecto es `spring-boot:run` así que se puede ejecutar
escribiendo`mvnw` (Windows), o `./mvnw` (Mac y Linux). Tomcat arranca en el puerto 8080:
[http://localhost:8080](http://localhost:8080) y está configurado para que abra el navegador directamente.

## Versión de producción

Para generar el código de producción, se debe utilizar el perfil `production` de
Maven: `mvnw clean package -Pproduction`
(Windows) o `./mvnw clean package -Pproduction` (Mac y Linux).

En el jar de producción se incluyen todas las dependencias JavaScript y los recursos del front-end (en modo de
desarrollo los recursos se obtienen / empaqueten / sirven de forma dinámica a través
de [Vite dev-server](https://vitejs.dev/))

El jar de producción se puede arrancar sencillamente con:
`java -jar target/wise-1.0-SNAPSHOT.jar`

## Consideraciones generales

Toda la funcionalidad de búsqueda y filtrado está directamente soportada por la base de datos. Ahora sólo hay los
índices primarios y el de la relación entre eventos y fuentes, pero en la realidad al menos la marca de tiempo debería
estar indexada probablemente. Si vamos a hacer búsquedas por rango de valores, entonces el valor debería seguramente
estar indexada también. Dado que los eventos se generan cada vez que arranca el ejemplo, la base de datos es en memoria
y realmente la lista es pequeña, no merece la pena darle más vueltas.

Se usa [Spring Data](https://spring.io/projects/spring-data) y jdbc para la capa de persistencia o patrón del
repositorio. Al final la query del `EventDto` se monta concatenando cadenas, pero en el mundo real sería mucho más
sensato utilizar
[jOOQ](https://www.jooq.org) o [Querydsl](http://querydsl.com/) que dan al menos una interfaz con tipos para escribir
`sql`.

Spring Data es conveniente, pero tiene algunas limitaciones como no soportar (aún) paginación basada en cursores. Si
potencialmente la lista de eventos fuera muy larga y nos quisiéramos remontar muy atrás, paginar con `OFFSET` es mala
idea, ya que estamos obligando a la base de datos a recuperar registros para luego descartarlos.

El api de Vaadin, en todo
caso ([DataProvider](https://vaadin.com/api/platform/23.1.1/com/vaadin/flow/data/provider/DataProvider.html))
también espera una query con `offset` y `limit` y escapa al objeto de este ejemplo el implementarlo.

No hay capa de servicio porque el código es demasiado pequeño para que merezca la pena. La vista usa directamente los
repositorios.

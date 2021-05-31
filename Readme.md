# groceries-organizer

## How to use

### Maven
The dependencies are managed with maven.
The project was developed using Java 16 therefore the `maven-compiler-plugin` is configured to use this version as source and target.

To install everything simply call:
```bash
mvn clean install
```
This will download all dependencies if they are missing and generate some classes used by `JOOQ`.
These are located under `plugins/target/generated-sources/jooq`.
Maybe you have to mark ths directory as "generated Sources Root" in intelliJ if it doesn't detects it automatically.

### Postgres
The storage Backend used for this project is a postgres database.
The easiest way to set this up is to use Docker.
A Dockerfile which install all the required schemas is included under `plugins/src/main/resources/postgres.dockerfile`.
To build the image simply run:
```bash
docker build -f plugins/src/main/resources/postgres.dockerfile plugins/src/main/resources -t postgres:groceries-organizer 
```
Then you need to set a username and password and run the container:
```bash
docker run --rm --name postgres -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -p 5432:5432 -d postgres:groceries-organizer
```
Then you need to create a properties-file under ` plugins/src/main/resources/postgres-db.properties` which contains all the information required to connect:
```properties
jdbc.user=admin
jdbc.password=admin
jdbc.url=jdbc:postgresql://localhost:5432/groceries-organizer
```

### Webserver
The project is compiled into a single `war`-file which can be used be tomcat.
It was tested with tomcat 9.0.46 as version 10 doesn't work with the `javax` packages.

The easiest way to start the server is to run it via intelliJ.
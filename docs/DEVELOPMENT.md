# Building and developing quak

This document describes quak's build and development environment.

## Development requirements

Quak relies on Apache Maven >= 3.8.x and is being developed with Java 17 (but it will probably also run with older versions).

## Running the application in dev mode

Quarkus, the foundation for quak, offers a ["dev mode"](https://quarkus.io/guides/dev-ui) that enables live coding. You can run quak in dev mode like this:

```shell script
./mvnw compile quarkus:dev
```

Afterwards, the dev user interface can be accessed via http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware however that this is no [über-jar](https://imagej.net/develop/uber-jars), because it lacks the dependencies found in the`target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating native executables

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/quack-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Creating docker images

While we provide pre-built docker images of quak on docker hub (see https://hub.docker.com/r/bestsolutionat/quak), you can also build your own images.

Have a look inside the `docker/` directory to find a couple of different Dockerfiles, allowing you to build different types of images.
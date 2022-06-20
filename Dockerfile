####
# This Dockerfile is used in order to build a quak container that runs the Quarkus application in JVM mode
#
# Build the image like:
#
# docker build --build-arg VERSION=VERSION_NO --build-arg REVISION=REVISION_NO -f src/main/docker/Dockerfile.jvm -t quarkus/quak-jvm .
#
# Run the container like:
#
# docker run -i -u $(id -u) --mount type=bind,source=$(pwd)/repositories/,target=/quak/repositories/ --mount type=bind,source=$(pwd)/config/,target=/quak/config/ --rm -p 8080:8080 quarkus/quak-jvm
#
###

# Build stage
FROM eclipse-temurin:18.0.1_10-jdk-jammy AS build

COPY . /home/quak-build

WORKDIR /home/quak-build

#RUN mvn -N wrapper:wrapper
#RUN ./mvnw package

RUN mvn package

# Build-time metadata stage
ARG BUILD_DATE
ARG VERSION
ARG REVISION
LABEL org.opencontainers.image.created=$BUILD_DATE \
      org.opencontainers.image.title="quak" \
      org.opencontainers.image.description="Lightweight Maven repository server which uses Quarkus, the Supersonic Subatomic Java Framework." \
      org.opencontainers.image.url="https://www.bestsolution.at/" \
      org.opencontainers.image.revision=$REVISION \
      org.opencontainers.image.source="https://github.com/BestSolution-at/quak" \
      org.opencontainers.image.documentation="https://github.com/BestSolution-at/quak" \
      org.opencontainers.image.vendor="BestSolution.at" \
      org.opencontainers.image.version=$VERSION

# Run stage
FROM eclipse-temurin:18.0.1_10-jre-jammy

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

# Configure the JAVA_OPTIONS, you can add -XshowSettings:vm to also display the heap size.
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

WORKDIR /quak

# quak User ID
ARG QUAK_UID=1000

# User with given ID is added.
RUN useradd -d /quak -s /bin/bash -u $QUAK_UID quakrunner

RUN [ -d repositories ] || mkdir repositories
RUN chown quakrunner repositories

# Switch to quak runner user by default.
USER quakrunner

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=build /home/quak-build/target/quarkus-app/lib/ lib/
COPY --from=build /home/quak-build/target/quarkus-app/*.jar .
COPY --from=build /home/quak-build/target/quarkus-app/app/ app/
COPY --from=build /home/quak-build/target/quarkus-app/quarkus/ quarkus/

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]

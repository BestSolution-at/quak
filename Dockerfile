####
# This Dockerfile is used in order to build a quak container that runs the Quarkus application in JVM mode
#
# Build the image like:
#
# docker build -t quak:latest .
#
# Run the container like:
#
# docker run -i -u $(id -u) --mount type=bind,source=$(pwd)/repositories/,target=/quak/repositories/ --mount type=bind,source=$(pwd)/config/,target=/quak/config/ --rm -p 8080:8080 quak:latest
#
###

FROM eclipse-temurin:17-jre-jammy

# For vulnerabilities be up-to-date
RUN apt-get update && apt-get upgrade -y

LABEL org.opencontainers.image.title="quak" \
      org.opencontainers.image.description="Lightweight Maven repository server which uses Quarkus, the Supersonic Subatomic Java Framework." \
      org.opencontainers.image.url="https://www.bestsolution.at/" \
      org.opencontainers.image.source="https://github.com/BestSolution-at/quak" \
      org.opencontainers.image.documentation="https://github.com/BestSolution-at/quak" \
      org.opencontainers.image.vendor="BestSolution.at"

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
COPY ./target/quarkus-app/lib/ lib/
COPY ./target/quarkus-app/*.jar .
COPY ./target/quarkus-app/app/ app/
COPY ./target/quarkus-app/quarkus/ quarkus/

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]

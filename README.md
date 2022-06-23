# quak Project

quak is a Maven repository server which uses Quarkus, the Supersonic Subatomic Java Framework. 

It can be used to deploy or install artifacts. 

Since it is based on Quarkus, it is fast and lightweight compared to Reposilite, Nexus and Archiva. 

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## What it is and what it is not

quak is:
 - a Maven repository server **only**
 - lightweight
 - fast
 - secure
 - platform intependent

quak is **NOT**
 - a Maven proxy
	

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

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

## Configuration

Following is a sample configuration file placed under the path: `$PWD/config/application.properties`.

```
quarkus.oauth2.enabled = false
quarkus.oidc.enabled = false
quarkus.http.port = 8089
quarkus.http.limits.max-body-size = 1000M

quak.repositories[0].name = blueprint
quak.repositories[0].storage-path = repos/blueprint
quak.repositories[0].base-url = /at/bestsolution/blueprint
quak.repositories[0].allow-redeploy = true
quak.repositories[0].is-private = false
```

| Configuration | Explanation | Default Value
|-----------------|:-------------|:-------------|
| `quarkus.http.port` 									| Port Quarkus is running on 														| 8080 (Quarkus)
| `quarkus.http.limits.max-body-size`     				| Upload limit Quarkus has 															| 10240K (Quarkus)
| `quarkus.oauth2.enabled`								| If OAuth2 is enabled																| false (quak)
| `quarkus.oidc.enabled`									| If OpenID Connect is enabled														| false (quak)
| `quarkus.oidc.authentication.user-info-required`	| If OpenID Connect is used, this must set to true for quak to acquire user info	| true (quak)
| `quak.repositories[0].name`    						| Name of the repository 															| 
| `quak.repositories[0].storage-path`    				| Location of the artifacts 														| 
| `quak.repositories[0].base-url`    					| Repository is served at 															| 
| `quak.repositories[0].allow-redeploy`    				| If the same version can be redeployed  											| true (quak)
| `quak.repositories[0].is-private`    					| True if it is a private repository, false if not									| false (quak)

Please note that `quak.repositories` configuration is an **array** and one instance of quak can serve as many as repositories defined here.

Given "blueprint" name is and example repository to explain how to define a repository.

For information about Quarkus configuration please see: https://quarkus.io/guides/config-reference .


## Authentication and authorization

Both authentication and authorization can be satisfied in quak with a simple configuration. Users can be defined with read/write permissions on different paths and repositories.

Please see ['Authentication and authorization in quak'](docs/AUTHORIZATION.md) for more details.


## FAQ

 - Q: How do I give a user permission for paths with an exception of a particular one?

Configuration field `quak.user-permissions[].url-paths[]` is a list of permitted paths, written with regular expressions. Defining a user permission over repository with a negated regular expression can satisfy the condition:

```
quak.user-permissions[0].username = user1
quak.user-permissions[0].repository-name = blueprint
quak.user-permissions[0].url-paths[0] = /at/bestsolution/(?!.*exceptThisPath).*
quak.user-permissions[0].may-publish = true
```

The Configuration above allows user1 to generally access paths which begin with `/at/bestsolution/*`, but denies access to paths which contain `exceptThisPath`. So for example, user1 would have access denied to `/at/bestsolution/exceptThisPath` or also to `/at/bestsolution/example/exceptThisPath`.

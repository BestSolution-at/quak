# Configuring quak

This document describes how to configure quak.

## Configuration

Because quak is based upon Quarkus, it follows its conventions how to configure Quarkus applications.

This boils down to two things:

1. all Quarkus configuration options can be used for quak as well (but only very few should)
2. quak looks for the configuration file at certain directories predefined by Quarkus

Typically, the configuration file will reside in `$PWD/config/application.properties` as shown in the following  directory structure, produced when you build quak:

```.
.
├── app
│   └── quak-999-SNAPSHOT.jar
├── config
│   └── application.properties
├── lib
│   ├── boot
│   │   ├── io.quarkus.quarkus-bootstrap-runner-x.y.z.Final.jar
│   │   ├── [...]
│   └── main
│       ├── com.fasterxml.jackson.core.jackson-annotations-x.y.z.jar
│       ├── [...]
├── quarkus
│   ├── generated-bytecode.jar
│   ├── [...]
├── quarkus-run.jar
└── repositories
```

Apart from that, the location of the configuration file can be specified various ways (environment variables, ...), as described in the [Quarkus documentation](https://Quarkus.io/guides/config-reference).

## Configuration options

| Configuration                             | Explanation                                                  | Default Value        |
| ----------------------------------------- | :----------------------------------------------------------- | :------------------- |
| `quak.auth-type`                          | one of<br />* `http-basic`<br />* `JWT`<br />* `oauth2`<br />* `oidc`<br /><br />see [Authentication and Authorization](docs/AUTH.md) for more information and configuration options. | **http-basic**       |
| `quak.basic-users[].username`             | the unique name of a user, only for HTTP Basic authentication |                      |
| `quak.basic-users[].password`             | an user's encrypted password for HTTP Basic authentication   |                      |
| `quak.http.host`                          | IP address or hostname that quak will be listening on        | **127.0.0.1**        |
| `quak.http.max-body-size`                 | the max size of uploaded artifacts                           | **10240K**           |
| `quak.http.port`                          | the TCP port quak will be listening on                       | **8080**             |
| `quak.log-file.enable`                    | per default, quak logs to STDOUT/STDERR, but you can also enable logging to a file | **false**            |
| `quak.log-file.level`                     | the log level to be logged                                   | **INFO**             |
| `quak.log-file.path`                      | if file logging has been enabled, the path to the logfile    | `$PWD/logs/quak.log` |
| `quak.repositories-base-path`             | the absolute base path where repositories will be stored     | `$PWD/repositories`  |
| `quak.repositories[].allow-redeploy`      | are redeployments of identical artifact versions allowed (hint: for **snapshots**, this is typically **true** whereas for **release** repositories this is typically **false**) | **true**             |
| `quak.repositories[].base-url`            | the URL the repository will be served at                     |                      |
| `quak.repositories[].name`                | the verbose, unique name of the repository                   |                      |
| `quak.repositories[].is-private`          | private repositories can only be accessed by authenticated users | **false**            |
| `quak.repositories[].storage-path`        | the relative, physical storage location of the artifacts<br />The absolute path is constructed via `quak.repositories-base-path+quak.repositories[].storage-path` |                      |
| `quak.user-permissions[].may-publish`     | if true, the user may deploy artifacts                       | **false**            |
| `quak.user-permissions[].repository-name` | the repository-name to define permissions for                |                      |
| `quak.user-permissions[].username`        | the username to define permissions for                       |                      |
| `quak.user-permissions[].url-paths[]`     | [Java regex](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) defining a path within a repository to define permissions for |                      |

Please note that `quak.basic-users[]`, `quak.repositories[]`, `quak.user-permissions[]` and `quak-user-permissions[].url-paths[]` are **arrays**.

One instance of quak can serve as many repositories as defined in the `quak.repositories[]` array.

For information about general Quarkus configuration please see: https://Quarkus.io/guides/config-reference .

Also, please be aware that quak needs to be restarted after you change its configuration.

## Example configuration

The following is a sample configuration file to be placed in`$PWD/config/application.properties`

```
# quak listens on http://127.0.0.1:8089
quak.http.host = 127.0.0.1
quak.http.port = 8089
# the max size of uploaded artifacts is 1000 MB
quak.max-body-size = 1000M

# enable logging to a file
quak.log-file.enable = true
quak.log-file.path = /var/log/quak.log
quak.log-file.level = WARN

# absolute base path to store repositories under
quak.repositories-base-path = /var/quak

# define a repository called "blueprint"
quak.repositories[0].name = blueprint
# the repository's physical storage location is $PWD/repos/blueprint
quak.repositories[0].storage-path = repos/blueprint
# the URL path to access the repository will be /at/bestsolution/blueprint
# so the entire URL will look something like this:
# http://localhost:8089/at/bestsolution/blueprint/
quak.repositories[0].base-url = /at/bestsolution/blueprint
# same version artifacts can be redeployed => this is typically true for
# snaphot repositories
quak.repositories[0].allow-redeploy = true
# this is a public repository, it won't require authentication to browse it,
# but uploads will always require authentication nevertheless
quak.repositories[0].is-private = false
```

Also, there is a more comprehensive document sample in the `docs/` folder.

## Authentication and authorization

For detailed information on how to configure both authentication and authorization in quak, please have a look in the dedicated [Authentication and authorization in quak](docs/AUTH.md) documentation.


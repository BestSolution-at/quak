# Running quak

This document describes quak's runtime options.

## Filesystem structure

Typically, quak will use a file system structure as shown below:

```.
.
├── app
│   └── quak-999-SNAPSHOT.jar
├── config
│   └── application.properties
├── lib
│   ├── [...]
├── quarkus
│   ├── [...]
├── quarkus-run.jar
└── repositories
```

In the context of running quak, the crucial parts are:

* ` ./config/application.properties`
  As described in [Configuring quak](CONFIGURATION.md), this is quak's main config file
* `./quarkus-run.jar`
  the runnable JAR file
* `./repositories/`
  the base directory for all artifacts.

## Standalone

As described in [Building and developing quak](docs/DEVELOPMENT.md), you can easily build your own version of quak.

After you built quak and [configured it](docs/CONFIGURATION.md) to your needs, you can run it like this:

`java -jar ./quarkus-app/quarkus-run.jar`

In the future, we also intend to provide native packages for debian and FreeBSD.

## Docker

This is the most simple way to run quak. We provide pre-built docker images on https://hub.docker.com/r/bestsolutionat/quak

Say for example you have your main configuration in a local folder `/opt/quak/etc`/, want to store your artifacts in the local folder `/opt/quak/repos/`, and intend to run quak as the currently logged in user, you can run quak like this:

```
$ docker pull bestsolutionat/quak:latest
$ docker run \
-i \ 
-u $(id -u) \
--mount type=bind,source=/opt/quak/etc/,target=/quak/config/ \
--mount type=bind,source=/opt/quak/repos/,target=/quak/repositories/ \
-p 8080:8080\ 
quak:latest
```

This will run quak interactively (`-i`) with the currently logged in user's id (`-u $(id -u)`), mount the local `/opt/quak/etc/` directory inside the container as `/quak/config/` and the local `/opt/quak/repos/` directory inside the container as `/quak/repositores/`. 

If you don't specify a user id (`-u`), then the quak container will default to `uid=1000`

In order to publish artifacts, the user running quak must have write permissions for the local directory mounted as into `/quak/repositores/`.


## FAQ

- **Q1: Can I run quak behind a load balancer?**
  Yes, absolutely. Only be sure that the load balancer passes on the fitting HTTP authentication headers.

- **Q2: How to serve the repository with HTTPS?**
  While you can achieve this by configuring the underlying Quarkus to use an SSL certificate, we do not recommend this setup but instead to use a load balancer that deals with SSL/TLS termination.

- **Q3: Why doesn't quak pick up my configuration changes?**
  Quak does not watch the configuration file for changes. Instead, you have to restart quak to re-read the configuration. Given quak's low profile, this nothing to worry about, because quak restarts very fast.




# quak repository server

[BestSolution.at](https://www.bestsolution.at) **quak** is an open source, low profile [Apache Maven](https://maven.apache.org/) repository server based on [Quarkus](https://quarkus.io/), the Supersonic Subatomic Java Framework.

It can currently be used to deploy and host maven artifacts.

Because it is based on Quarkus, it is both fast and lightweight compared to other repository servers. It is built around the idea to be reduced to the max, meaning that while it may lack some fancy features, it is intended to provide what you need >90% of the time.

If you want to learn more about quak, please visit its website https://github.com/BestSolution-at/quak

## What is quak?

**quak IS:**

* a Maven repository server (but we may extend it for other formats in the future)
* lightweight
* fast
* production ready and battle tested
* secure
* platform independent

**quak is NOT:**

* a Maven proxy (yet)

## Rationale behind quak

Almost all our Java software projects are built upon Maven and we have been using Maven repository servers for just as long. In the past, we mostly relied upon the [Nexus Repository Manager](https://www.sonatype.com/products/nexus-repository), but we have also worked with others, like [Reposilite](https://reposilite.com).

Unfortunately, in our environments, we found them lacking for a number of reasons: they were either too complex to manage, and/or did not fit well into today's k8s/terraform world, and/or were not stable enough, and/or were lacking the security features we needed.

So our internal requirements were basically as follows:

- Fast, stable and secure
- Minimal resource consumption
- Simple, templating friendly, config-file based administration
- k8s ready

Our initial focus is on hosting maven artifacts, including deployment. In the future, we may also add other repository formats, such as npm, see the [roadmap](docs/ROADMAP.md) for quak.

## Main features

* 100% Open Source under the [Apache 2.0 License](LICENSE)
* Intended to be very resource friendly
* Out-of-the box HTTP Basic, JWT, OAuth2, and OIDC support for authentication
* Public and private repositories
* Platform independent
* No BLOB but real file system storage (facilitating backup and native file system de-duplication)
* k8s ready

## Getting quak

There are various ways how to get quak:

* Compile your own quak as described in [Building and developing](docs/DEVELOPMENT.md)
* Use the docker images from https://hub.docker.com/r/bestsolutionat/quak
* In the future, we intend to offer debian and FreeBSD packages as well

## Configuring and running quak

The configuration is explained in a dedicated [configuration](docs/CONFIGURATION.md) document and how to run it, for example using docker, is explained in  [running quak](docs/RUNNING.md).

## Roadmap

See the dedicated [Roadmap](docs/ROADMAP.md) document.

## Building and developing quak

See the dedicated [Building and developing](docs/DEVELOPMENT.md) document.

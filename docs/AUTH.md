# Authentication and authorization in quak

Configuring both authentication and authorization can be done in the main configuration file.

## Authentication

While authentication is always required for publishing artifacts, it is optional for read access to a repository.

Quak understands various types of authentication, but one instance of quak can only have one type of authentication configured at a time. If you require different types of authentication for different repositories, start up another instance of quak instead.

**This is a work in progress, more documentation including tutorials how to use gitlab or github as authentication providers will follow soon.**

### HTTP Basic authentication

Defining a user for HTTP Basic authentication is very straight forward. It only requires a username and a password. Passwords must be stored as **BCrypt hashed** values of actual passwords.

```
quak.auth-type = http-basic

# define some users
quak.basic-users[0].username = user1
quak.basic-users[0].password = $2a$10$Nfw5dwvR8ly0HPzhVE92TuqqUORzw1WBs9f4hmPBdkaqctmJQVtNu
```

For creating the hashed passwords, you can for example use the Apache `htpasswd` command line tool: 

`echo "aFancyPassword" | htpasswd -bnBC 10 "" - | tr -d ':\n'`

For further information please see the [htpasswd documentation](https://httpd.apache.org/docs/2.4/programs/htpasswd.html).

The created password hash must be copied into the password field of the relevant user.

### WIP: Authentication using JWT tokens

**Warning: documentation on JWT is work in progress**!

Authentication can be done with [JWT tokens](https://jwt.io/) as well. It is based upon [SmallRye JWT](https://smallrye.io/docs/smallrye-jwt/index.html). This way, authentication can be "delegated" to an external service providing the JWT token and typically passing it to quak using an in-between load balancer. Tokens created by an valid issuer will be verified by quak and the username contained in the token will be used by quak.

The following is a sample configuration provided by Quarkus, setting public key location, key file name and issuer of tokens. Having this configuration, quak will be able to authorize Maven requests with JWT tokens:

```
quak.auth-type = jwt

# the public key to verify the tokens against
quak.jwt.issuer.publickey = /path/to/issuers/publicKey.pem
# shades mp.jwt.verify.publickey.location = /path/to/issuers/publicKey.pem

# the expected value of the JWT iss (issuer) claim
quak.jwt.issuer.name = https://www.example.com/quak/test
# shades mp.jwt.verify.issuer = https://www.example.com/quak/test
```

The issuer's public key file must be provided by the issuer.

Once this configuration is done correctly, usernames defined in the `user-permissions` array will be able to access the fitting repositories based on the token sent along witht the Maven requests.

### WIP: Authentication using OAuth 2.0 tokens

**Warning: documentation on OAuth 2.0 tokens is work in progress!**

Quak allows authentication with [OAuth2 opaque tokens](https://oauth.net/2/access-tokens/). For this to work, quak needs to be configured like this:

```
quak.auth-type = oauth2
# shades quarkus.oauth2.enabled = true

# used to identify a request 
quak.oauth2.client-id = client-id
# shades quarkus.oauth2.client-id = client_id

# the shared secret
quak.oauth2.client-secret = client_secret
# shades quarkus.oauth2.client-secret = client_secret

# the token presented to quak/Quarkus will be sent to this URL for verification and introspection
quak.oauth2.introspection-url = https://oauth2.example.com/token/introspect
# shades quarkus.oauth2.introspection-url = https://oauth2.example.com/token/introspect
```

When configuration is done correctly, requests with OAuth2 opaque tokens will be validated by sending them to the introspection URL. After validation, the username will be extracted and compared to the `user-permissions` array in order to see if the user has permissions for the given repository.

The user-id will be extracted from the authentication token. Depending on the used Oauth 2.0 provider that can for example be a numerical id or also a verbose user name. There is no "general rule" unfortunately. Depending on what the OAuth 2.0 provider sends to quak as an user-id, this will have to be used as `quak.user-permissions[].username`.

### WIP: Authentication using OpenID Connect (OIDC) tokens

**Warning: documentation on OIDC is work in progress**!

Authentication can also be based upon [OpenID Connect](https://connect2id.com/learn/openid-connect). The following configuration enables OIDC token authentication in quak:

```
quak.auth-type = oidc
# shades quarkus.oidc.enabled = true

# the server to talk to for authentication
quak.oidc.auth-server-url = https://oidc.example.com/
# shades quarkus.oidc.auth-server-url = https://oidc.example.com/

# the client_id identifying quak to the auth server
quak.oidc.client-id = client_id
# shades quarkus.oidc.client-id = client_id

# shared client secret
quak.oidc.shared-secret = client_secret
# shades quarkus.oidc.credentials.secret = client_secret

# required to get additonal user data (like usernames) from the auth-server
quak.oidc.extended-user-info = true
# shades quarkus.oidc.authentication.user-info-required = true
```

When configuration is done correctly, requests with OpenID Connect access tokens will be validated by sending them to the authentication server. After validation, the extracted username will be queried to see if the user exists in quak and has rights on the given repository.

The user-id will be extracted from the authentication token. Depending on the used OIDC provider that can for example be a numerical id or also a verbose user name. There is no "general rule" unfortunately. Depending on what the OIDC provider sends to quak as an user-id, this will have to be used as `quak.user-permissions[].username`.

> **_NOTE:_** Configuration option `quak.oidc.extended-user-info = true` is mandatory for quak in order to acquire the unique user identifier. The user attribute `sub` granted by the OpenID Connect authentication provider must be used as a username in quak and a user permission must be configured as described in section [Authorization](#Authorization) below.

## Authorization

Currently, quak only knows two types of permissions: read and write.

Read permissions are mainly relevant for private repositories. Without read permission, users wont be able to access it at all.

Write permissions are generally required to publish artifacts (ie. `mvn deploy`). Even when the repository itself is public, publishing anything requires an authenticated and authorized user.

### Defining user permissions

In order to have authorized access to repositories, user permissions must be defined as follows:

```
# unique username matching the authentication type. 
# For example, for http-basic authentication, this will come from quak.basic-users[].username
quak.user-permissions[0].username = user1
# unique repository name, as defined in quak.repositories[].name
quak.user-permissions[0].repository-name = blueprint
# the list of URLs to configure permissions for
quak.user-permissions[0].url-paths[0] = /.*
# this enables the permission to publish artifacts, defaults to false
quak.user-permissions[0].may-publish = true
```

- `username`: unique username which has access.
- `repository-name`: unique repository name which is accessed.
- `url-paths[]`: a list of permitted paths, written as [Java regular expressions](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html).
- `may-publish`: true if the user has publishing rights, false for read-only access

### Accessing private repositories

Private repositories are intended to prevent anonymous users from accessing a repository. In order to access such a respository, all users have to be authenticated and have a `quak.user-permissons[]` entry matching username and repository.

The following example shows how to define a private repository. The `is-private` configuration keyword is used to seperate between public and private repositories:

```
quak.repositories[0].name = blueprint
# because is-private = true, this repository is a private one
quak.repositories[0].is-private = true
[...]

# when using http-basic authentication
quak.basic-users[0].username = user1
[...]

# this allows "user1" to access the private "blueprint" repo
quak.user-permissions[0].username = user1
quak.user-permissions[0].repository-name = blueprint
quak.user-permissions[0].url-paths[0] = /.*
# may-publish is false per default, so we don't need to specify it here
# quak.user-permissions[0].may-publish = false
```

### Publishing artifacts

In order to publish artifacts both in public and private repositories alike, users need to have set the `may-publish` option to true:

The following example shows how to define private repository. The `is-private` configuration keyword is used to seperate between public and private repositories:

```
quak.repositories[0].name = blueprint
[...]

# when using http-basic authentication
quak.basic-users[0].username = user1
[...]

# this allows "user1" to publish artifacts in the "blueprint" repo
quak.user-permissions[0].username = user1
quak.user-permissions[0].repository-name = blueprint
quak.user-permissions[0].url-paths[0] = /.*
quak.user-permissions[0].may-publish = true
```

## FAQ

 - **Q1: How do I allow a user to access a general path except one (or more) particular one?**

Configuration field `quak.user-permissions[].url-paths[]` is a list of ordinary [Java regular expressions](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html), describing paths the permission should be applied to. In order to exclude one (or more) directories/files, just negate the regular expression as shown below:

```
quak.user-permissions[0].username = user1
quak.user-permissions[0].repository-name = blueprint
quak.user-permissions[0].may-publish = true
# user may publish inside /at/bestsolution/ and below
quak.user-permissions[0].url-paths[0] = /at/bestsolution/.*
# ... except any directory that contains "exceptThisPath" in its name
quak.user-permissions[0].url-paths[1] = /at/bestsolution/(?!.*exceptThisPath).*
```

The Configuration above allows `user1` to generally access all paths beginning with `/at/bestsolution/*`, but denies access to paths which contain `exceptThisPath`. So in this example, the user would be denied to deploy artifacts to `/at/bestsolution/exceptThisPath` or also to `/at/bestsolution/example/exceptThisPath`.


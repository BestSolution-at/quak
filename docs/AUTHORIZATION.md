# Authentication and authorization in quak

Both authentication and authorization can be satisfied in quak with a simple configuration. Users can be defined with read/write permissions on different paths and repositories.


## Private/Public respositories

Repositories can be defined as private or public. Authentication will be required for both read and write requests if it is a private one. Public repositories will only require authentication on write requests.

Following is sample configuration showing how to define a private repository. When `is-private` value is false it is a public repository.

```
quak.repositories[0].name = blueprint
quak.repositories[0].storage-path = repos/blueprint
quak.repositories[0].base-url = /at/bestsolution/blueprint
quak.repositories[0].allow-redeploy = true
quak.repositories[0].is-private = true
```

## Defining users and basic authentication

Defining a user for Basic Authentication is very straight forward. It requires username and password. Passwords must be stored as **BCrypt hashed** values of actual passwords.

```
quak.users[0].username = user1
quak.users[0].password = $2a$10$Nfw5dwvR8ly0HPzhVE92TuqqUORzw1WBs9f4hmPBdkaqctmJQVtNu
```

For hashing passwords, Apache command line tool *htpasswd* can be used. It can create password hashes with a call like: `htpasswd -bnBC 10 "" myPassword | tr -d ':\n'`

For further information please see: [htpasswd man page](https://httpd.apache.org/docs/2.4/programs/htpasswd.html)

Created password hash must be copied to password field of user.


> **_NOTE:_** This way of user creation is not mandatory if token authentication will be used.


## Defining user permissions and authorization

In order to have authorized access to repositories, user permissions must be defined in configuration as following sample:

```
quak.user-permissions[0].username = user1
quak.user-permissions[0].repository-name = blueprint
quak.user-permissions[0].url-paths[0] = /.*
quak.user-permissions[0].may-publish = true
```

`username` : unique username which has access.

`repository-name` : unique repository name which is accessed.

`url-paths[]` : a list of permitted paths, written with regular expressions.

`may-publish` : true if user has right to publish, false if has only read right.


## Authentication with JWT tokens

Authentication can be done with JWT tokens as well. In this way, Maven users do not need to enter username and password credentials, but only JWT tokens when using quak. Tokens created by an issuer will be verified by quak and token claimed username will be used.

Following is a sample configuration provided by Quarkus, setting public key location, key file name and issuer of tokens. Having this configuration quak will be able to authorize Maven requests with tokens.

```
mp.jwt.verify.publickey.location = /path/toKey/publicKey.pem
mp.jwt.verify.issuer = https://www.bestsolution.at/quak/test
quarkus.native.resources.includes = publicKey.pem
```

Issuer uses a private key to create tokens and quak uses a public key to verify them. This private and public key pair can be created with *OpenSSL* as shown in following commands:

```
openssl genrsa -out rsaPrivateKey.pem 2048
openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem
```

Once this configuration is done correctly, usernames defined in user-permissions list, will be able to access permitted repositories provided that tokens are available in Maven requests.


## Authentication with OAuth2 tokens

Quarkus includes authentication with OAuth2 opaque tokens. With following configuration, quak can be configured to have OAuth2 token authentication enabled.

```
quarkus.oauth2.enabled = true
quarkus.oauth2.client-id = client_id
quarkus.oauth2.client-secret = client_secret
quarkus.oauth2.introspection-url = https://introspectionUrl/token/introspect
```

When configuration is done correctly, requests with OAuth2 opaque tokens will be validated by sending them to introspection URL. After validation, extracted username will be queried to see if user has permission rights on quak for given repository.

# Authorization in quak

Authorization can be satisfied in Quak with a simple configuration. Users can be defined with read/write permissions on different paths and repositories.


## Private/Public respositories

Repositories can be defined as private or public. Authentication will be required for both read and write requests if it is a private one. Public repositories will only require authentication on write requests.

Following is sample configuration showing how to define a private repository. When `is-write` value is false it is a public repository.

```
quak.repositories[0].name = blueprint
quak.repositories[0].storage-path = repos/blueprint
quak.repositories[0].base-url = /at/bestsolution/blueprint
quak.repositories[0].allow-redeploy = true
quak.repositories[0].is-private = true
```

## Defining users

Defining a user for Basic Authentication is very straight forward. It requires username and password. Passwords must be stored as **BCrypt hashed** values of actual passwords.

```
quak.users[0].username = user1
quak.users[0].password = $2a$10$Nfw5dwvR8ly0HPzhVE92TuqqUORzw1WBs9f4hmPBdkaqctmJQVtNu
```

**This way of user creation is not mandatory if token authorization will be used.**


## Defining user permissions

In order to have authorized access to repositories, user permissions must be defined in configuration as following sample:

```
quak.user-permissions[0].username = user1
quak.user-permissions[0].repository-name = blueprint
quak.user-permissions[0].url-paths[0] = /.*
quak.user-permissions[0].is-write = true
```

`username` : unique username which has access.
`password` : unique repository name which is accessed.
`urlpaths[]` : a list of permitted paths, written with regular expressions.
`is-write` : true if it is a write permission, false if it is only a read permission.


## Token authorization

Authorization can be done with tokens as well. Following is a sample configuration, setting public key location, key file name and issuer of tokens.

```
mp.jwt.verify.publickey.location = /path/toKey/publicKey.pem
mp.jwt.verify.issuer = https://www.bestsolution.at/quak/test
quarkus.native.resources.includes = publicKey.pem
```

Once this configuration is done correctly, usernames defined in user-permissions list, will be able access permitted repositories provided that tokens are available in Maven requests.

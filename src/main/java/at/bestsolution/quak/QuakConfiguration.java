/*
 * ----------------------------------------------------------------
 * Original File Name: QuakConfiguration.java
 * Creation Date:      28.01.2022
 * Description: Class file of quak configuration.       
 * ----------------------------------------------------------------

 * ----------------------------------------------------------------
 * Copyright (c) 2022 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved .
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software must not be used, redistributed or based from in
 * any other than the designated way without prior explicit written
 * permission by BestSolution.at.
 * -----------------------------------------------------------------
 */

package at.bestsolution.quak;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Represents quak configurations. 
 */
@StaticInitSafe
@ConfigMapping( prefix = "quak" )
public interface QuakConfiguration {
	
	/**
	 * @return
	 * 		HTTP configuration of quak.
	 */
	public HTTP http();
	
	/**
	 * @return
	 * 		JWT configuration of quak.
	 */
	public JWT jwt();
	
	/**
	 * @return
	 * 		OAuth2 configuration of quak.
	 */
	public OAuth2 oauth2();
	
	/**
	 * @return
	 * 		OpenID Connect configuration of quak.
	 */
	public OIDC oidc();
	
	/**
	 * @return
	 * 		maximum number of clean up tasks which can run concurrently.
	 */
	@WithDefault("5")
	public int maxConcurrentCleanUpTasks();
	
	/**
	 * @return
	 * 		base path where repositories are stored.
	 */
	@WithDefault("repositories/")
	public Path repositoriesBasePath();
	
	/**
	 * @return list of repositories defined in quak configuration.
	 */
	public List<Repository> repositories();
	
	/**
	 * @return 
	 * 		List of basic authentication users defined in quak configuration.
	 */
	public List<BasicUser> basicUsers();
	
	/**
	 * @return list of user permissions defined in quak configuration.
	 */
	public List<UserPermission> userPermissions();
	
	/**
	 * Represents a quak http configuration.
	 */
	public interface HTTP {
		
		/**
		 * @return 
		 * 		the TCP port quak will be listening on.
		 */
		public int port();
		
		/**
		 * @return 
		 * 		the TCP host quak will be listening on.
		 */
		public String host();
		
		/**
		 * @return 
		 * 		the max size of uploaded artifacts.
		 */
		public String maxBodySize();
	}
	
	/**
	 * Represents a quak JWT configuration.
	 */
	public interface JWT {
		
		/**
		 * @return 
		 * 		location of JWT token issuer public key.
		 */
		public Optional<String> issuerPublicKey();
		
		/**
		 * @return 
		 * 		name of JWT token issuer.
		 */
		public Optional<String> issuerName();
	}
	
	/**
	 * Represents a quak OAuth2 configuration.
	 */
	public interface OAuth2 {
		
		/**
		 * @return 
		 * 		if the OAuth2 extension is enabled.
		 */
		public boolean enabled();
		
		/**
		 * @return 
		 * 		OAuth2 client id used to validate the token. Mandatory if the OAuth2 is enabled.
		 */
		public Optional<String> clientId();
		
		/**
		 * @return 
		 * 		OAuth2 client secret used to validate the token. Mandatory if the OAuth2 is enabled.
		 */
		public Optional<String> clientSecret();
		
		/**
		 * @return 
		 * 		OAuth2 introspection endpoint URL used to validate the token. Mandatory if the extension is enabled.
		 */
		public Optional<String> introspectionUrl();
	}
	
	/**
	 * Represents a quak OpenID Connect configuration.
	 */
	public interface OIDC {
		
		/**
		 * @return 
		 * 		if the OIDC extension is enabled.
		 */
		public boolean enabled();
		
		/**
		 * @return 
		 * 		OIDC client id used to validate the token. Mandatory if the OIDC is enabled.
		 */
		public Optional<String> clientId();
		
		/**
		 * @return 
		 * 		OIDC client secret used to validate the token. Mandatory if the OIDC is enabled.
		 */
		public Optional<String> sharedSecret();
		
		/**
		 * @return 
		 * 		base URL of OIDC server, for example, https://host:port/auth.
		 */
		public Optional<String> authServerUrl();
		
		/**
		 * @return 
		 * 		if this property is set to 'true' then an OIDC UserInfo endpoint will be called. Must be true for quak OIDC authentication.
		 */
		public boolean userInfoRequired();
	}
	
	/**
	 * Represents a repository configuration.
	 */
	public interface Repository {
		/**
		 * @return name of repository. It is a unique identifier.
		 */
		public String name();
		
		/**
		 * @return storage path of repository.
		 */
		public Path storagePath();
		
		/**
		 * @return base URL of repository.
		 */
		public String baseUrl();
		
		/**
		 * @return true if it is a private repository, false if not. Default is false. 
		 * Private repositories are read and write protected. Public repositories are only write protected.
		 */
		@WithDefault("false")
		public boolean isPrivate();
		
		/**
		 * @return true if re-deploying is allowed, false if not. Default is true.
		 */
		@WithDefault("true")
		public boolean allowRedeploy();
		
		/**
		 * @return
		 * 		clean up configuration of repository.
		 */
		public CleanUp cleanUp();
	}
	
	/**
	 * Represents a clean up configuration.
	 */
	public interface CleanUp {
		/**
		 * @return
		 * 		true if clean up with hard delete, false if soft delete.
		 */
		@WithDefault("true")
		public boolean hardDelete();
	}
	
	/**
	 * Represents a quak basic authentication user.
	 */
	public interface BasicUser {
		/**
		 * @return 
		 * 		username of quak user. It is a unique identifier.
		 */
		public String username();
		
		/**
		 * @return 
		 * 		password of quak user, hashed with BCrypt hash algorithm.
		 */
		public String password();
	}
	
	/**
	 * Represents a quak user permission.
	 */
	public interface UserPermission {
		/**
		 * @return username for whom permission is given.
		 */
		public String username();
		
		/**
		 * @return name of repository which grants permission.
		 */
		public String repositoryName();
		
		/**
		 * @return URL paths in regular expressions. Expressions define 
		 * all URL paths which related user is permitted.
		 */
		public List<String> urlPaths();
		
		/**
		 * @return true if user has right to publish, false if has only read right.
		 */
		@WithDefault("false")
		public boolean mayPublish();
	}
}
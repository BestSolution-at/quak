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

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Represents a quak configuration. Contains a list of repository configurations.
 */
@StaticInitSafe
@ConfigMapping( prefix = "quak" )
public interface QuakConfiguration {
	
	/**
	 * @return list of repositories defined in quak configuration.
	 */
	public List<Repository> repositories();
	
	/**
	 * @return list of users defined in quak configuration.
	 */
	public List<User> users();
	
	/**
	 * @return list of user permissions defined in quak configuration.
	 */
	public List<UserPermission> userPermissions();
	
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
	}
	
	/**
	 * Represents a quak user.
	 */
	public interface User {
		/**
		 * @return username of quak user. It is a unique identifier.
		 */
		public String username();
		
		/**
		 * @return password of quak user, hashed with BCrypt hash algorithm.
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
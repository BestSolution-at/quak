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
	
	public List<Repository> repositories();
	public List<User> users();
	public List<UserPermission> userPermissions();
	
	/**
	 * Represents a repository configuration.
	 */
	public interface Repository {
		public String name();
		public Path storagePath();
		public String baseUrl();
		
		@WithDefault("false")
		public boolean isPrivate();
		
		@WithDefault("true")
		public boolean allowRedeploy();
	}
	
	/**
	 * Represents a quak user.
	 *
	 */
	public interface User {
		public String username();
		public String password();
	}
	
	/**
	 * Represents a quak user permission.
	 *
	 */
	public interface UserPermission {
		public String username();
		public String repositoryName();
		public List<String> paths();
		
		@WithDefault("false")
		public boolean isWrite();
	}
}

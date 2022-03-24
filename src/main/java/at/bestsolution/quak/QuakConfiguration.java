package at.bestsolution.quak;

import java.nio.file.Path;
import java.util.List;

import io.smallrye.config.ConfigMapping;

/**
 * Represents a quak configuration. Contains a list of repository configurations.
 */
@ConfigMapping( prefix = "quak" )
public interface QuakConfiguration {
	
	public List<Repository> repositories();
	
	/**
	 * Represents a repository configuration.
	 */
	public interface Repository {
		public String name();
		public Path storagePath();
		public String baseUrl();
		public boolean allowRedeploy();
	}
}

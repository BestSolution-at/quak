package at.bestsolution.quak;

import java.nio.file.Path;
import java.util.List;

import io.smallrye.config.ConfigMapping;

@ConfigMapping( prefix = "quak" )
public interface QuakConfiguration {
	public List<Repository> repositories();
	
	public interface Repository {
		public String name();
		public Path storagePath();
		public String baseUrl();
		public boolean allowRedeploy();
	}
}

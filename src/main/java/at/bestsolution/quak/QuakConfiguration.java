package at.bestsolution.quak;

import java.nio.file.Path;
import java.util.List;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping( prefix = "quak" )
public interface QuakConfiguration {
	public List<Repository> repositories();
	
	public interface Repository {
		public String name();
		public Path storagePath();
		public String baseUrl();
		
		@WithDefault("true")
		public boolean allowRedeploy();
	}
}

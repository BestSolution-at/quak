package at.bestsolution.quak;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class QuakTestProfile implements QuarkusTestProfile {

    /**
     * Returns additional config to be applied to the test. This
     * will override any existing config (including in application.properties),
     * however existing config will be merged with this (i.e. application.properties
     * config will still take effect, unless a specific config key has been overridden).
     */
    @Override
    public Map<String, String> getConfigOverrides() {
    	Map<String, String> testConfigurations = new HashMap<String, String>();
    	testConfigurations.put( "quak.repositories[0].name", "blueprint" );
    	testConfigurations.put( "quak.repositories[0].storage-path", "repos/blueprint" );
    	testConfigurations.put( "quak.repositories[0].base-url", "/at/bestsolution/blueprint" );
    	testConfigurations.put( "quak.repositories[0].allow-redeploy", "true" );
    	
        return testConfigurations;
    }
}

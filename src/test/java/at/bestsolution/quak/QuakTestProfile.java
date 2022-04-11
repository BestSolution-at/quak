/*
 * ----------------------------------------------------------------
 * Original File Name: QuakTestProfile.java
 * Creation Date:      29.03.2022
 * Description: Profile class file for quak tests.       
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

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * Test profile class for basic tests.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakTestProfile implements QuarkusTestProfile {
	
	public static final String GOOD_USERNAME = "user1";
	public static final String GOOD_PASSWORD = "pass123";
	public static final String NAME = "blueprint";
	public static final String STORAGE_PATH = "repos/blueprint";
	public static final String BASE_URL = "/at/bestsolution/blueprint";
	public static final String ALLOW_REDEPLOY = "true";
	public static final String DO_NOT_ALLOW_REDEPLOY = "false";
	public static final String MAX_BODY_SIZE = "10M";

    /**
     * @return Returns additional basic quak configuration to be applied to the test. 
     */
    @Override
    public Map<String, String> getConfigOverrides() {
    	Map<String, String> testConfigurations = new HashMap<String, String>();
    	testConfigurations.put( "quak.repositories[0].name", NAME );
    	testConfigurations.put( "quak.repositories[0].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[0].base-url", BASE_URL );
    	testConfigurations.put( "quak.repositories[0].allow-redeploy", ALLOW_REDEPLOY );
    	testConfigurations.put( "quak.users[0].username", GOOD_USERNAME );
    	testConfigurations.put( "quak.users[0].password", GOOD_PASSWORD );
    	testConfigurations.put( "quarkus.http.limits.max-body-size", MAX_BODY_SIZE );
    	
        return testConfigurations;
    }
}

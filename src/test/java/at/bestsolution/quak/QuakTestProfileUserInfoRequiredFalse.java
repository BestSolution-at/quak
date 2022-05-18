/*
 * ----------------------------------------------------------------
 * Original File Name: QuakTestProfileUserInfoRequiredFalse.java
 * Creation Date:      18.05.2022
 * Description: Test profile for wrong user-info-required setup for 
 * OpenID Connect       
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

import java.util.Map;

/**
 * Test profile for wrong user-info-required setup for OpenID Connect.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakTestProfileUserInfoRequiredFalse extends QuakTestProfile {
	
	private static final String AUTH_SERVER_URL = "http://testUrl";
	private static final String CLIENT_ID = "test_id";
	private static final String CLIENTS_SECRET = "test_secret";
	
    /**
     * @return Returns additional quak configuration for wrong user-info-required setup for OpenID Connect. 
     */
    @Override
    public Map<String, String> getConfigOverrides() {
    	Map<String, String> testConfigurations = super.getConfigOverrides();
    	testConfigurations.put( "quarkus.oidc.authentication.user-info-required", Boolean.FALSE.toString() );
    	testConfigurations.put( "quarkus.oidc.enabled", Boolean.TRUE.toString() );
    	testConfigurations.put( "quarkus.oidc.auth-server-url", AUTH_SERVER_URL );
    	testConfigurations.put( "quarkus.oidc.client-id", CLIENT_ID );
    	testConfigurations.put( "quarkus.oidc.credentials.secret", CLIENTS_SECRET );
        return testConfigurations;
    }
}

/*
 * ----------------------------------------------------------------
 * Original File Name: QuakTestProfileAuthorization.java
 * Creation Date:      19.04.2022
 * Description: Test profile for authorization tests.      
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
 * Test profile for authorization tests.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakTestProfileAuthorization extends QuakTestProfile {
	
	public static final String USERNAME_ADMIN = "admin";
	public static final String REGEX_MATCH_ALL = "/*";
	public static final String REPOSITORY_NAME_UNAUTHORIZED = "blueprintUnauthorized";
	public static final String REPOSITORY_NAME_READ_ONLY = "blueprintReadOnly";
	public static final String BASE_URL_UNAUTHORIZED = "/unauthorized/at/bestsolution/";
	public static final String BASE_URL_READ_ONLY = "/readOnly/at/bestsolution/";
	public static final String BASE_URL_SUBPATH = "/at/bestsolution/blueprint/sub/path";

    /**
     * @return Returns additional basic quak configuration for authorization tests.
     */
    @Override
    public Map<String, String> getConfigOverrides() {
    	Map<String, String> testConfigurations = super.getConfigOverrides();
    	// Insert Admin User
    	testConfigurations.put( "quak.users[1].username", USERNAME_ADMIN );
    	testConfigurations.put( "quak.users[1].password", GOOD_PASSWORD_HASH );
    	// Insert Permissions
    	testConfigurations.put( "quak.permissions[1].username", GOOD_USERNAME );
    	testConfigurations.put( "quak.permissions[1].repository-name", REPOSITORY_NAME_READ_ONLY );
    	testConfigurations.put( "quak.permissions[1].paths[0]", REGEX_MATCH_ALL );
    	testConfigurations.put( "quak.permissions[1].is-write", READ_PERMISSION );
    	testConfigurations.put( "quak.permissions[2].username", USERNAME_ADMIN );
    	testConfigurations.put( "quak.permissions[2].repository-name", REPOSITORY_NAME_READ_ONLY );
    	testConfigurations.put( "quak.permissions[2].paths[0]", REGEX_MATCH_ALL );
    	testConfigurations.put( "quak.permissions[2].is-write", WRITE_PERMISSION );
    	// Insert Repositories
    	testConfigurations.put( "quak.repositories[1].name", REPOSITORY_NAME_UNAUTHORIZED );
    	testConfigurations.put( "quak.repositories[1].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[1].base-url", BASE_URL_UNAUTHORIZED );
    	testConfigurations.put( "quak.repositories[1].is-private", PRIVATE_REPOSITORY );
    	testConfigurations.put( "quak.repositories[2].name", REPOSITORY_NAME );
    	testConfigurations.put( "quak.repositories[2].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[2].base-url", BASE_URL_SUBPATH );
    	testConfigurations.put( "quak.repositories[2].is-private", PRIVATE_REPOSITORY );
    	testConfigurations.put( "quak.repositories[3].name", REPOSITORY_NAME_READ_ONLY );
    	testConfigurations.put( "quak.repositories[3].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[3].base-url", BASE_URL_READ_ONLY );
    	testConfigurations.put( "quak.repositories[3].is-private", PRIVATE_REPOSITORY );
        return testConfigurations;
    }
}
